package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.Hospital;
import com.blooddonation.service.HospitalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles hospital registration and login.
 * Split out of the original HospitalController to keep auth concerns isolated.
 */
@Controller
@RequestMapping("/hospital")
public class HospitalAuthController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/register")
    String showRegistrationForm(Model model) {
        if (!model.containsAttribute("hospital")) {
            model.addAttribute("hospital", new Hospital());
        }
        return "hospital/hospitalRegistration";
    }

    @PostMapping("/register")
    String registerHospital(@ModelAttribute("hospital") Hospital hospital,
                            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                            RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.title("Facility name", hospital.getName()),
                Validate.hospitalLicense("Clinical license ID", hospital.getLicenseNumber()),
                Validate.email("Email", hospital.getEmail()),
                Validate.phone("Contact number", hospital.getContactNumber()),
                Validate.password("Password", hospital.getPasswordHash()),
                Validate.match("Passwords", hospital.getPasswordHash(), confirmPassword)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            redirectAttributes.addFlashAttribute("hospital", hospital);
            return "redirect:/hospital/register";
        }

        try {
            hospitalService.registerHospital(hospital);
            redirectAttributes.addFlashAttribute("successMessage", "Hospital registered successfully! Please sign in.");
            return "redirect:/hospital/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("hospital", hospital);
            return "redirect:/hospital/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "hospital/hospitalLogin";
    }

    @GetMapping("/logout")
    public String logoutHospital(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out successfully.");
        return "redirect:/hospital/login";
    }

    @PostMapping("/login")
    public String loginHospital(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        String error = Validate.first(
                Validate.required("Email", email),
                Validate.required("Password", password)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/hospital/login";
        }

        try {
            Hospital hospital = hospitalService.authenticateHospital(email, password);
            session.setAttribute("hospital", hospital);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + hospital.getName() + "!");
            return "redirect:/hospital/dashboard"; // අවශ්‍ය පරිදි dashboard path එක වෙනස් කරගත හැක
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hospital/login";
        }
    }
}
