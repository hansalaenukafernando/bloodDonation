package com.blooddonation.repository;

import com.blooddonation.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndAccountType(String tokenHash, String accountType);

    Optional<PasswordResetToken> findTopByEmailAndAccountTypeOrderByCreatedAtDesc(String email, String accountType);

    // Derived delete queries must run inside a transaction (the service methods are @Transactional)
    void deleteByEmailAndAccountType(String email, String accountType);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
