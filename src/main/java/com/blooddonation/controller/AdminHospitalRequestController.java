package com.blooddonation.controller;

import com.blooddonation.entity.BloodRequest; // BloodRequest entity එක නිවැරදිව import කර ඇත
import com.blooddonation.service.HospitalBloodRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/hospital-requests")
public class AdminHospitalRequestController {

    @Autowired
    private HospitalBloodRequestService requestService;

    // 1. Hospital Requests ලැයිස්තුව පෙන්වීම
    @GetMapping
    public String showHospitalRequests(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("hospitalRequests", requestService.getAllRequests());
        return "admin/adminHospitalRequests";
    }

    // 1b. Hospital Requests History ලැයිස්තුව පෙන්වීම (Delivered / Rejected)
    @GetMapping("/history")
    public String showHospitalRequestHistory(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("hospitalRequestHistory", requestService.getDeliveryHistory());
        return "admin/adminHospitalRequestsHistory";
    }

    // 2. Request එකක Status එක වෙනස් කිරීම (Approve, Reject, Complete)
    @PostMapping("/update-status/{id}")
    public String updateRequestStatus(HttpSession session,
                                      @PathVariable("id") Integer requestId,
                                      @RequestParam("status") String status,
                                      RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        try {
            requestService.updateStatus(requestId, status);
            ra.addFlashAttribute("successMessage", "Hospital request status updated to " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/hospital-requests";
    }
}