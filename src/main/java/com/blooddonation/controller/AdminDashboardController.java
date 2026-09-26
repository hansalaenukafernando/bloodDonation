package com.blooddonation.controller;

import com.blooddonation.service.AdminDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles the admin dashboard view.
 * Split out of the original AdminController to keep this concern isolated.
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping({"/dashboard", ""})
    public String showAdminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        // ඩෑෂ්බෝඩ් එකට අවශ්‍ය සංඛ්‍යාලේඛන එකතු කිරීම
        model.addAttribute("totalDonors", adminDashboardService.getTotalDonorsCount());
        model.addAttribute("totalHospitals", adminDashboardService.getTotalHospitalsCount());
        model.addAttribute("activeDeliveries", adminDashboardService.getActiveDeliveriesCount());
        model.addAttribute("pendingTests", adminDashboardService.getPendingTestsCount());
        model.addAttribute("systemAlerts", adminDashboardService.getSystemAlerts());
        model.addAttribute("pendingApprovals", adminDashboardService.getPendingApprovals());

        return "admin/adminDashboard"; // templates/admin/adminDashboard.html වෙත යොමු කරයි
    }
}
