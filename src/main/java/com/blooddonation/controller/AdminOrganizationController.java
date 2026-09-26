package com.blooddonation.controller;

import com.blooddonation.entity.Organization;
import com.blooddonation.service.AdminOrganizationService;
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

import java.util.List;

@Controller
@RequestMapping("/admin/organizations")
public class AdminOrganizationController {

    @Autowired
    private AdminOrganizationService adminOrgService;

    @GetMapping
    public String showOrganizations(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        List<Organization> organizations = adminOrgService.getAllOrganizations();
        model.addAttribute("organizations", organizations);
        return "admin/adminOrganizations";
    }

    @PostMapping("/update")
    public String updateOrganization(@RequestParam("orgId") Integer orgId,
                                     @RequestParam("orgName") String orgName,
                                     @RequestParam("orgType") String orgType,
                                     @RequestParam(value = "regNumber", required = false) String regNumber,
                                     @RequestParam("address") String address,
                                     @RequestParam("coordinatorName") String coordinatorName,
                                     @RequestParam("email") String email,
                                     @RequestParam("phone") String phone,
                                     HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        String error = Validate.first(
                Validate.required("Organization", orgId),
                Validate.title("Organization name", orgName),
                Validate.required("Organization type", orgType),
                Validate.optionalText("Registration number", regNumber, 50),
                Validate.text("Address", address, 5, 255),
                Validate.name("Coordinator name", coordinatorName),
                Validate.email("Email", email),
                Validate.phone("Phone number", phone)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/admin/organizations";
        }

        try {
            adminOrgService.updateOrganization(orgId, orgName, orgType, regNumber, address, coordinatorName, email, phone);
            ra.addFlashAttribute("successMessage", "Organization details updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/organizations";
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam("orgId") Integer orgId,
                               @RequestParam("status") String status,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminOrgService.updateOrganizationStatus(orgId, status);
            ra.addFlashAttribute("successMessage", "Organization status changed to " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update status.");
        }
        return "redirect:/admin/organizations";
    }
}