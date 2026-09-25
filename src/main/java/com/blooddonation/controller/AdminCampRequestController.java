package com.blooddonation.controller;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.service.AdminCampRequestService;
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
@RequestMapping("/admin/camp-requests")
public class AdminCampRequestController {

    @Autowired
    private AdminCampRequestService adminCampRequestService;

    @GetMapping
    public String showCampRequests(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        List<CampRequest> requests = adminCampRequestService.getAllCampRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("today", java.time.LocalDate.now());
        return "admin/adminCampRequests";
    }

    // Camp Requests History Page එක පෙන්වීම (Completed / Cancelled camp requests)
    @GetMapping("/history")
    public String showCampRequestHistory(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        List<CampRequest> history = adminCampRequestService.getCampRequestHistory();
        model.addAttribute("history", history);
        return "admin/adminCampRequestsHistory";
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam("requestId") Integer requestId,
                               @RequestParam("status") String status,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminCampRequestService.updateCampStatus(requestId, status);
            ra.addFlashAttribute("successMessage", "Camp request status updated to " + status + " successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/camp-requests";
    }
}