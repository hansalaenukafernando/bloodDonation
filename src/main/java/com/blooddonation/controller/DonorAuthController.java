package com.blooddonation.controller;

import com.blooddonation.util.Validate;
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
 * Handles donor registration and login.
 * Split out of the original DonorController to keep auth concerns isolated.
 */
@Controller
@RequestMapping("/donor")
public class DonorAuthController {

    @Autowired
    private DonorService donorService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "donor/donorRegistration";
    }

    @PostMapping("/register")
    public String registerDonor(
            @RequestParam("name") String name,
            @RequestParam("nic") String nic,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("bloodGroup") String bloodGroup,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("address") String address,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.name("Full name", name),
                Validate.nic("NIC number", nic),
                Validate.email("Email", email),
                Validate.bloodGroup("Blood group", bloodGroup),
                Validate.phone("Contact number", contactNumber),
                Validate.text("Address", address, 5, 255),
                Validate.password("Password", password),
                Validate.match("Passwords", password, confirmPassword)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/donor/register";
        }

        try {
            Donor newDonor = new Donor();
            newDonor.setName(name);
            newDonor.setNic(nic);
            newDonor.setEmail(email);
            newDonor.setPasswordHash(password);
            newDonor.setBloodGroup(bloodGroup);
            newDonor.setContactNumber(contactNumber);
            newDonor.setAddress(address);

            donorService.registerDonor(newDonor);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
            return "redirect:/donor/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/donor/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "donor/donorLogin";
    }

    @PostMapping("/login")
    public String loginDonor(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.required("Email", email),
                Validate.required("Password", password)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/donor/login";
        }

        try {
            Donor loggedInDonor = donorService.authenticateDonor(email, password);
            session.setAttribute("donor", loggedInDonor);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + loggedInDonor.getName() + "!");
            return "redirect:/donor/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/donor/login";
        }
    }
}
