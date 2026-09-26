package com.blooddonation.controller;

import com.blooddonation.entity.AccountType;
import com.blooddonation.service.PasswordResetService;
import com.blooddonation.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Forgot / reset password for donors, hospitals and organizations.
 *
 *   GET  /{role}/forgot-password   -> enter email
 *   POST /{role}/forgot-password   -> sends the reset email
 *   GET  /{role}/reset-password    -> page opened from the email link (?token=...)
 *   POST /{role}/reset-password    -> saves the new password
 *
 * {role} is one of: donor, hospital, organization
 */
@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // ---------------------------------------------------------------
    // Step 1: ask for the reset email
    // ---------------------------------------------------------------

    @GetMapping("/{role}/forgot-password")
    public String showForgotPasswordForm(@PathVariable("role") String role, Model model) {
        model.addAttribute("accountType", resolve(role));
        return "auth/forgotPassword";
    }

    @PostMapping("/{role}/forgot-password")
    public String handleForgotPassword(@PathVariable("role") String role,
                                       @RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        AccountType type = resolve(role);
        String forgotUrl = "redirect:/" + type.getPath() + "/forgot-password";

        String error = Validate.email("Email", email);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return forgotUrl;
        }

        passwordResetService.requestReset(type, email);

        // Same response whether or not the email exists (don't leak which emails are registered)
        redirectAttributes.addFlashAttribute("emailSent", true);
        redirectAttributes.addFlashAttribute("sentTo", email.trim());
        return forgotUrl;
    }

    // ---------------------------------------------------------------
    // Step 2: open the link from the email and choose a new password
    // ---------------------------------------------------------------

    @GetMapping("/{role}/reset-password")
    public String showResetPasswordForm(@PathVariable("role") String role,
                                        @RequestParam(value = "token", required = false) String token,
                                        Model model) {
        AccountType type = resolve(role);
        model.addAttribute("accountType", type);
        model.addAttribute("token", token);
        model.addAttribute("tokenValid", passwordResetService.isTokenValid(type, token));
        return "auth/resetPassword";
    }

    @PostMapping("/{role}/reset-password")
    public String handleResetPassword(@PathVariable("role") String role,
                                      @RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        AccountType type = resolve(role);
        String resetUrl = "redirect:/" + type.getPath() + "/reset-password";

        String error = Validate.first(
                Validate.password("New password", password),
                Validate.match("Passwords", password, confirmPassword)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            redirectAttributes.addAttribute("token", token);
            return resetUrl;
        }

        try {
            passwordResetService.resetPassword(type, token, password);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset successful! Please sign in with your new password.");
            return "redirect:/" + type.getPath() + "/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addAttribute("token", token);
            return resetUrl;
        }
    }

    // ---------------------------------------------------------------

    /** Maps the URL segment to an AccountType; anything else (e.g. /admin/...) is a 404. */
    private AccountType resolve(String role) {
        AccountType type = AccountType.fromPath(role);
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return type;
    }
}
