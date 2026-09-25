package com.blooddonation.controller;

import com.blooddonation.entity.Hospital;
import com.blooddonation.service.AdminHospitalService;
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
@RequestMapping("/admin/hospitals")
public class AdminHospitalController {

    @Autowired
    private AdminHospitalService adminHospitalService;

    @GetMapping
    public String showHospitals(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        List<Hospital> hospitals = adminHospitalService.getAllHospitals();
        model.addAttribute("hospitals", hospitals);
        return "admin/adminHospitals";
    }

    @PostMapping("/update")
    public String updateHospital(@RequestParam("hospitalId") Integer hospitalId,
                                 @RequestParam("name") String name,
                                 @RequestParam("licenseNumber") String licenseNumber,
                                 @RequestParam("email") String email,
                                 @RequestParam("contactNumber") String contactNumber,
                                 HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        String error = Validate.first(
                Validate.required("Hospital", hospitalId),
                Validate.title("Facility name", name),
                Validate.hospitalLicense("License number", licenseNumber),
                Validate.email("Email", email),
                Validate.phone("Contact number", contactNumber)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/admin/hospitals";
        }

        try {
            adminHospitalService.updateHospital(hospitalId, name, licenseNumber, email, contactNumber);
            ra.addFlashAttribute("successMessage", "Hospital details updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/hospitals";
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam("hospitalId") Integer hospitalId,
                               @RequestParam("status") String status,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminHospitalService.updateHospitalStatus(hospitalId, status);
            ra.addFlashAttribute("successMessage", "Hospital status changed to " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update status.");
        }
        return "redirect:/admin/hospitals";
    }
}