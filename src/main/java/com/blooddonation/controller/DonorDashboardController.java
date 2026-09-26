package com.blooddonation.controller;

import com.blooddonation.entity.Donor;
import com.blooddonation.entity.DonorRequest;
import com.blooddonation.service.DonorRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles the donor dashboard view.
 * Split out of the original DonorController to keep dashboard concerns isolated.
 */
@Controller
@RequestMapping("/donor")
public class DonorDashboardController {

    @Autowired
    private DonorRequestService requestService;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        Integer donorId = loggedInDonor.getDonorId();
        long totalDonations = requestService.getTotalDonationsCount(donorId);
        List<DonorRequest> requests = requestService.getRequestsByDonor(donorId);
        LocalDate nextEligibleDate = requestService.getNextEligibleDate(donorId);
        boolean isEligible = requestService.isEligibleToDonate(donorId);

        model.addAttribute("donor", loggedInDonor);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("requests", requests);
        model.addAttribute("nextEligibleDate", nextEligibleDate);
        model.addAttribute("isEligible", isEligible);

        return "donor/donorDashboard";
    }
}
