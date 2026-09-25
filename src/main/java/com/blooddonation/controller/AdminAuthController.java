package com.blooddonation.controller;

import com.blooddonation.entity.Admin;
import com.blooddonation.service.AdminService;
import com.blooddonation.util.Validate;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles admin login.
 * Split out of the original AdminController to keep auth concerns isolated.
 */
@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    /**
     * Shows the Admin login page.
     */
    @GetMapping("/login")
    public String showAdminLoginForm() {
        return "admin/adminLogin";
    }

    /**
     * Handles Admin login authentication.
     */
    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.required("Username", username),
                Validate.required("Password", password)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/admin/login";
        }

        try {
            Admin loggedInAdmin = adminService.authenticateAdmin(username, password);

            // Save admin details in session
            session.setAttribute("admin", loggedInAdmin);

            redirectAttributes.addFlashAttribute("successMessage", "Welcome, " + loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")");

            // Redirect to admin dashboard (you can create this page later)
            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/login";
        }
    }
}
