package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.Organization;
import com.blooddonation.service.OrganizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles organization registration and login.
 * Split out of the original OrganizationController to keep auth concerns isolated.
 */
@Controller
@RequestMapping("/organization")
public class OrganizationAuthController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "organization/organizationRegistration";
    }

    @PostMapping("/register")
    public String registerOrganization(@RequestParam("orgName") String orgName,
                                       @RequestParam("orgType") String orgType,
                                       @RequestParam(value = "regNumber", required = false) String regNumber,
                                       @RequestParam("address") String address,
                                       @RequestParam("coordName") String coordName,
                                       @RequestParam("coordEmail") String coordEmail,
                                       @RequestParam("coordPhone") String coordPhone,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.title("Organization name", orgName),
                Validate.required("Organization type", orgType),
                Validate.optionalText("Registration number", regNumber, 50),
                Validate.text("Address", address, 5, 255),
                Validate.name("Coordinator name", coordName),
                Validate.email("Coordinator email", coordEmail),
                Validate.phone("Coordinator phone", coordPhone),
                Validate.password("Password", password),
                Validate.match("Passwords", password, confirmPassword)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/register";
        }

        try {
            organizationService.registerOrganization(orgName, orgType, regNumber, address, coordName, coordEmail, coordPhone, password);
            redirectAttributes.addFlashAttribute("successMessage", "Organization registered successfully! Please sign in.");
            return "redirect:/organization/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/organization/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "organization/organizationLogin";
    }

    @GetMapping("/logout")
    public String logoutOrganization(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out successfully.");
        return "redirect:/organization/login";
    }

    @PostMapping("/login")
    public String loginOrganization(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.required("Email", email),
                Validate.required("Password", password)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/login";
        }

        try {
            Organization org = organizationService.authenticateOrganization(email, password);
            session.setAttribute("organization", org);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + org.getOrgName() + "!");
            return "redirect:/organization/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/organization/login";
        }
    }
}
