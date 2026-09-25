package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.Hospital;
import com.blooddonation.service.HospitalService;
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
 * Handles hospital profile viewing and updates (details + password).
 */
@Controller
@RequestMapping("/hospital/settings")
public class HospitalProfileController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        model.addAttribute("hospital", hospital);
        return "hospital/hospitalProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("licenseNumber") String licenseNumber,
                                @RequestParam("email") String email,
                                @RequestParam("contactNumber") String contactNumber,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        String error = Validate.first(
                Validate.title("Facility name", name),
                Validate.hospitalLicense("License number", licenseNumber),
                Validate.email("Email", email),
                Validate.phone("Contact number", contactNumber)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/hospital/settings";
        }

        try {
            Hospital updatedHospital = hospitalService.updateHospitalProfile(
                    hospital.getHospitalId(), name, licenseNumber, email, contactNumber);
            session.setAttribute("hospital", updatedHospital);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/hospital/settings";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        String error = Validate.first(
                Validate.required("Current password", currentPassword),
                Validate.password("New password", newPassword),
                Validate.match("New passwords", newPassword, confirmPassword)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/hospital/settings";
        }

        try {
            hospitalService.updatePassword(hospital.getHospitalId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/hospital/settings";
    }
}
