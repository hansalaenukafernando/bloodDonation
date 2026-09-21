package com.blooddonation.controller;

import com.blooddonation.entity.Donor;
import com.blooddonation.service.AdminDonorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/donors")
public class AdminDonorController {

    @Autowired
    private AdminDonorService adminDonorService;

    @GetMapping
    public String showDonorManagement(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<Donor> donors = adminDonorService.getAllDonors();
        model.addAttribute("donors", donors);
        return "admin/donorManagement";
    }

    @PostMapping("/update")
    public String updateDonor(@RequestParam("donorId") Integer donorId,
                              @RequestParam("name") String name,
                              @RequestParam("email") String email,
                              @RequestParam("contactNumber") String contactNumber,
                              @RequestParam("bloodGroup") String bloodGroup,
                              @RequestParam("address") String address,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDonorService.updateDonorByAdmin(donorId, name, email, contactNumber, bloodGroup, address);
            redirectAttributes.addFlashAttribute("successMessage", "Donor details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/donors";
    }

    @PostMapping("/status")
    public String changeStatus(@RequestParam("donorId") Integer donorId,
                               @RequestParam("status") String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDonorService.updateDonorStatus(donorId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Donor status changed to " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change donor status.");
        }
        return "redirect:/admin/donors";
    }
}