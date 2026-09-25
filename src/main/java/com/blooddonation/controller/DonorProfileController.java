package com.blooddonation.controller;

import com.blooddonation.entity.Donor;
import com.blooddonation.service.DonorService;
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
 * Handles donor profile viewing and updates (details + password).
 * Split out of the original DonorController to keep this concern isolated.
 */
@Controller
@RequestMapping("/donor/profile")
public class DonorProfileController {

    @Autowired
    private DonorService donorService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        model.addAttribute("donor", loggedInDonor);
        return "donor/donorProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("contactNumber") String contactNumber,
                                @RequestParam("address") String address,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        try {
            Donor updatedDonor = donorService.updateDonorProfile(
                    loggedInDonor.getDonorId(), name, email, contactNumber, address);
            session.setAttribute("donor", updatedDonor);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/donor/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        try {
            donorService.updatePassword(loggedInDonor.getDonorId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/donor/profile";
    }
}
