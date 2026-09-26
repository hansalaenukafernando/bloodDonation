package com.blooddonation.controller;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.entity.Organization;
import com.blooddonation.service.CampRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles the organization dashboard view.
 * Split out of the original OrganizationController to keep this concern isolated.
 */
@Controller
@RequestMapping("/organization")
public class OrganizationDashboardController {

    @Autowired
    private CampRequestService campRequestService;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        long totalDrives = campRequestService.getTotalCompletedDrives(org);
        int totalDonors = campRequestService.getTotalDonorsEngaged(org);
        int livesSaved = totalDonors * 3;
        CampRequest upcomingCamp = campRequestService.getUpcomingCamp(org);

        model.addAttribute("organization", org);
        model.addAttribute("totalDrives", totalDrives);
        model.addAttribute("totalDonors", totalDonors);
        model.addAttribute("livesSaved", livesSaved);
        model.addAttribute("upcomingCamp", upcomingCamp);
        return "organization/organizationDashboard";
    }
}
