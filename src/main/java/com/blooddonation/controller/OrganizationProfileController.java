package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.Organization;
import com.blooddonation.service.OrganizationService;
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
 * Handles organization profile viewing and updates (details + password).
 * Split out of the original OrganizationController to keep this concern isolated.
 */
@Controller
@RequestMapping("/organization/profile")
public class OrganizationProfileController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        model.addAttribute("organization", org);
        return "organization/organizationProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("orgName") String orgName,
                                @RequestParam("orgType") String orgType,
                                @RequestParam("address") String address,
                                @RequestParam("coordinatorName") String coordinatorName,
                                @RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                HttpSession session, RedirectAttributes ra) {

        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        String error = Validate.first(
                Validate.title("Organization name", orgName),
                Validate.required("Organization type", orgType),
                Validate.text("Address", address, 5, 255),
                Validate.name("Coordinator name", coordinatorName),
                Validate.email("Email", email),
                Validate.phone("Phone number", phone)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/profile";
        }

        try {
            organizationService.updateOrganizationProfile(org.getOrgId(), orgName, orgType, address, coordinatorName, email, phone);

            Organization updatedOrg = organizationService.findById(org.getOrgId()).orElse(org);
            session.setAttribute("organization", updatedOrg);

            ra.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organization/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session, RedirectAttributes ra) {

        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        String error = Validate.first(
                Validate.password("New password", newPassword),
                Validate.match("New passwords", newPassword, confirmPassword)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/profile";
        }

        try {
            organizationService.updateOrganizationPassword(org.getOrgId(), newPassword);
            ra.addFlashAttribute("successMessage", "Password updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organization/profile";
    }
}
