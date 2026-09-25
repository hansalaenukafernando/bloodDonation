package com.blooddonation.controller;

import com.blooddonation.service.LabTestOperationsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/blood-tests")
public class AdminBloodTestController {

    @Autowired
    private LabTestOperationsService labTestOperationsService;

    // 1. තවමත් ටෙස්ට් කිරීමට ඇති (Pending / Untested) Blood Bags පිටුව
    @GetMapping("/pending")
    public String showPendingTests(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        model.addAttribute("pendingBags", labTestOperationsService.getPendingBloodBags());
        return "admin/pendingBloodTests";
    }

    // 2. ටෙස්ට් කර අවසන් වූ (Tested History & Results) Blood Bags පිටුව
    @GetMapping("/history")
    public String showTestedHistory(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        model.addAttribute("testedBags", labTestOperationsService.getTestedBloodHistory());
        return "admin/testedBloodHistory";
    }
}