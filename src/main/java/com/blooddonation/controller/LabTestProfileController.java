package com.blooddonation.controller;

import com.blooddonation.entity.LabTester;
import com.blooddonation.service.LabTestAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles lab tester profile viewing and updates (details + password).
 * Split out of the original LabTestController to keep this concern isolated.
 */
@Controller
@RequestMapping("/labtest/profile")
public class LabTestProfileController {

    @Autowired
    private LabTestAuthService labTestAuthService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        model.addAttribute("tester", tester);
        return "labtest/labtestProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        try {
            LabTester updatedTester = labTestAuthService.updateProfile(tester.getTesterId(), name, email);
            session.setAttribute("labtester", updatedTester);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/labtest/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        try {
            labTestAuthService.updatePassword(tester.getTesterId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/labtest/profile";
    }
}
