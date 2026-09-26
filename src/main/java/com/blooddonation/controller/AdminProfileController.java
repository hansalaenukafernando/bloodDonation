package com.blooddonation.controller;

import com.blooddonation.entity.Admin;
import com.blooddonation.repository.AdminRepository;
import com.blooddonation.util.Validate;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/profile")
public class AdminProfileController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/admin/login";

        model.addAttribute("admin", admin);
        return "admin/adminProfile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/admin/login";

        String error = Validate.password("New password", newPassword);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/admin/profile";
        }

        try {
            Admin currentAdmin = adminRepository.findById(admin.getAdminId())
                    .orElseThrow(() -> new Exception("Admin not found"));

            currentAdmin.setPasswordHash(newPassword);
            adminRepository.save(currentAdmin);

            session.setAttribute("admin", currentAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Admin password updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/profile";
    }
}