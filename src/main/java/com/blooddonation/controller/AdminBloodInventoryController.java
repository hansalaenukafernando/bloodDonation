package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.service.BloodBagService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/inventory")
public class AdminBloodInventoryController {

    @Autowired
    private BloodBagService bloodBagService;

    // 1. View Inventory & Stock Summary Page
    @GetMapping
    public String showInventory(HttpSession session, Model model, RedirectAttributes ra) {
        // Admin කෙනෙක් Login වී ඇත්දැයි පරීක්ෂා කිරීම (Login වී නැත්නම් Login page එකට redirect වේ)
        // (ඔබේ project එකේ admin session attribute එක වෙනත් නමකින් තිබේ නම් එය මෙහි යොදන්න, උදාහරණයක් ලෙස "admin" හෝ "adminUser")
        if (session.getAttribute("admin") == null) {
            ra.addFlashAttribute("error", "Please sign in to access the admin portal.");
            return "redirect:/admin/login"; // ඔබේ Admin login URL එක මෙහි දෙන්න
        }

        bloodBagService.cleanupExpiredBags();

        model.addAttribute("inventoryList", bloodBagService.getAllBloodBags());

        model.addAttribute("stockA_pos", bloodBagService.getStockCount("A+"));
        model.addAttribute("stockA_neg", bloodBagService.getStockCount("A-"));
        model.addAttribute("stockB_pos", bloodBagService.getStockCount("B+"));
        model.addAttribute("stockB_neg", bloodBagService.getStockCount("B-"));
        model.addAttribute("stockAB_pos", bloodBagService.getStockCount("AB+"));
        model.addAttribute("stockAB_neg", bloodBagService.getStockCount("AB-"));
        model.addAttribute("stockO_pos", bloodBagService.getStockCount("O+"));
        model.addAttribute("stockO_neg", bloodBagService.getStockCount("O-"));

        return "admin/adminBloodInventory";
    }

    // 1b. View Used Blood Bags History Page
    @GetMapping("/history")
    public String showUsedHistory(HttpSession session, Model model, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            ra.addFlashAttribute("error", "Please sign in to access the admin portal.");
            return "redirect:/admin/login";
        }

        model.addAttribute("usedBags", bloodBagService.getUsedBloodBagsHistory());
        return "admin/adminBloodInventoryHistory";
    }

    // 2. Add New Blood Bag
    @PostMapping("/add")
    public String addInventoryUnit(HttpSession session,
                                   @RequestParam(value = "donorId", required = false) Integer donorId,
                                   @RequestParam("bloodGroup") String bloodGroup,
                                   @RequestParam("collectionDate") LocalDate collectionDate,
                                   @RequestParam("expiryDate") LocalDate expiryDate,
                                   RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        String error = Validate.first(
                Validate.bloodGroup("Blood group", bloodGroup),
                Validate.notFutureDate("Collection date", collectionDate),
                Validate.required("Expiry date", expiryDate),
                Validate.afterDate("Expiry date", expiryDate, "Collection date", collectionDate)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/admin/inventory";
        }

        try {
            bloodBagService.addBloodBag(donorId, bloodGroup, collectionDate, expiryDate);
            ra.addFlashAttribute("successMessage", "Blood bag added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error adding blood bag: " + e.getMessage());
        }
        return "redirect:/admin/inventory";
    }

    // 3. Delete Blood Bag
    @PostMapping("/delete/{id}")
    public String deleteInventoryUnit(HttpSession session, @PathVariable("id") Integer id, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        try {
            bloodBagService.deleteBloodBag(id);
            ra.addFlashAttribute("successMessage", "Blood bag removed from inventory.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to remove blood bag.");
        }
        return "redirect:/admin/inventory";
    }

    // 4. Cleanup Expired Bags
    @PostMapping("/cleanup-expired")
    public String cleanupExpired(HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        try {
            bloodBagService.cleanupExpiredBags();
            ra.addFlashAttribute("successMessage", "Expired blood bags have been successfully cleaned up.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to clean up expired bags.");
        }
        return "redirect:/admin/inventory";
    }
}