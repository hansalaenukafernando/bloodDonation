package com.blooddonation.service;

import com.blooddonation.entity.AccountType;
import com.blooddonation.entity.Donor;
import com.blooddonation.entity.Hospital;
import com.blooddonation.entity.Organization;
import com.blooddonation.entity.PasswordResetToken;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.OrganizationRepository;
import com.blooddonation.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * "Forgot password" logic shared by donors, hospitals and organizations.
 *
 * Flow:
 *   1. requestReset()  - creates a one-time token and emails a link
 *   2. isTokenValid()  - checked when the link is opened
 *   3. resetPassword() - sets the new password and deletes the token
 */
@Service
public class PasswordResetService {

    /** How long the emailed link works. */
    public static final int EXPIRY_MINUTES = 30;

    /** Minimum gap between two reset emails for the same account (stops email spamming). */
    private static final int COOLDOWN_SECONDS = 60;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired private DonorRepository donorRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /** Minimal info needed to address the email. */
    private record Account(String email, String displayName) {}

    /**
     * Sends a reset link if an active account with this email exists.
     * Deliberately returns nothing: the caller must show the same message either way,
     * so nobody can use this form to find out which emails are registered.
     */
    @Transactional
    public void requestReset(AccountType type, String rawEmail) {
        String email = rawEmail == null ? "" : rawEmail.trim();

        // housekeeping: drop expired tokens
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        Optional<Account> accountOpt = findActiveAccount(type, email);
        if (accountOpt.isEmpty()) {
            return;
        }
        Account account = accountOpt.get();

        Optional<PasswordResetToken> last =
                tokenRepository.findTopByEmailAndAccountTypeOrderByCreatedAtDesc(account.email(), type.name());
        if (last.isPresent() && last.get().getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(COOLDOWN_SECONDS))) {
            return; // asked too recently - the earlier email is still valid
        }

        // only one active link per account
        tokenRepository.deleteByEmailAndAccountType(account.email(), type.name());

        String rawToken = generateToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(account.email());
        token.setAccountType(type.name());
        token.setTokenHash(sha256(rawToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        tokenRepository.save(token);

        String link = trimTrailingSlash(baseUrl) + "/" + type.getPath() + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(account.email(), account.displayName(), type.getLabel(), link, EXPIRY_MINUTES);
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(AccountType type, String rawToken) {
        return findValidToken(type, rawToken).isPresent();
    }

    @Transactional
    public void resetPassword(AccountType type, String rawToken, String newPassword) throws Exception {
        PasswordResetToken token = findValidToken(type, rawToken)
                .orElseThrow(() -> new Exception("This reset link is invalid or has expired. Please request a new one."));

        // NOTE: passwords are stored the same way the rest of the app stores them (see the register / login code)
        switch (type) {
            case DONOR -> {
                Donor donor = donorRepository.findByEmail(token.getEmail())
                        .orElseThrow(() -> new Exception("Account not found."));
                donor.setPasswordHash(newPassword);
                donorRepository.save(donor);
            }
            case HOSPITAL -> {
                Hospital hospital = hospitalRepository.findByEmail(token.getEmail())
                        .orElseThrow(() -> new Exception("Account not found."));
                hospital.setPasswordHash(newPassword);
                hospitalRepository.save(hospital);
            }
            case ORGANIZATION -> {
                Organization org = organizationRepository.findByEmail(token.getEmail())
                        .orElseThrow(() -> new Exception("Account not found."));
                org.setPasswordHash(newPassword);
                organizationRepository.save(org);
            }
        }

        // one-time use: remove every token for this account
        tokenRepository.deleteByEmailAndAccountType(token.getEmail(), type.name());
    }

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    private Optional<PasswordResetToken> findValidToken(AccountType type, String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByTokenHashAndAccountType(sha256(rawToken.trim()), type.name())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    private Optional<Account> findActiveAccount(AccountType type, String email) {
        if (email.isEmpty()) {
            return Optional.empty();
        }
        return switch (type) {
            case DONOR -> donorRepository.findByEmail(email)
                    .filter(d -> !"Deactivated".equalsIgnoreCase(d.getStatus()))
                    .map(d -> new Account(d.getEmail(), d.getName()));
            case HOSPITAL -> hospitalRepository.findByEmail(email)
                    .filter(h -> !"Deactivated".equalsIgnoreCase(h.getStatus()))
                    .map(h -> new Account(h.getEmail(), h.getName()));
            case ORGANIZATION -> organizationRepository.findByEmail(email)
                    .filter(o -> !"Deactivated".equalsIgnoreCase(o.getStatus()))
                    .map(o -> new Account(o.getEmail(), o.getCoordinatorName()));
        };
    }

    /** 256 random bits, URL-safe. */
    private static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
