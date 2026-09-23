package com.blooddonation.controller;

import com.blooddonation.entity.LabTester;
import com.blooddonation.service.LabTestAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles lab tester login.
 * Split out of the original LabTestController to keep auth concerns isolated.
 */
@Controller
@RequestMapping("/labtest")
public class LabTestAuthController {

    @Autowired
    private LabTestAuthService labTestAuthService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "labtest/labtestLogin";
    }

    @GetMapping("/logout")
    public String logoutLabTester(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out successfully.");
        return "redirect:/labtest/login";
    }

    @PostMapping("/login")
    public String loginLabTester(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            LabTester loggedInTester = labTestAuthService.authenticateLabTester(email, password);
            session.setAttribute("labtester", loggedInTester);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + loggedInTester.getName() + "!");
            return "redirect:/labtest/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/labtest/login";
        }
    }
}
