package com.blooddonation.controller;

import com.blooddonation.entity.DonorRequest;
import com.blooddonation.service.AdminRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    @Autowired
    private AdminRequestService adminRequestService;

    @GetMapping
    public String showRequestManagement(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<DonorRequest> requests = adminRequestService.getAllRequests();
        model.addAttribute("requests", requests);
        return "admin/requestManagement";
    }

    // Donation Requests History Page එක පෙන්වීම (Completed / Cancelled requests)
    @GetMapping("/history")
    public String showRequestHistory(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<DonorRequest> history = adminRequestService.getRequestHistory();
        model.addAttribute("history", history);
        return "admin/requestHistory";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("requestId") Integer requestId,
                               @RequestParam("status") String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminRequestService.updateRequestStatus(requestId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Request status updated successfully! (Inventory updated if completed)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/requests";
    }
}