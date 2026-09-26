package com.blooddonation.controller;

import com.blooddonation.entity.Hospital;
import com.blooddonation.service.HospitalBloodRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles the hospital dashboard view.
 * Split out of the original HospitalController to keep this concern isolated.
 */
@Controller
@RequestMapping("/hospital")
public class HospitalDashboardController {

    @Autowired
    private HospitalBloodRequestService bloodRequestService;

    @GetMapping("/dashboard")
    public String showHospitalDashboard(HttpSession session, Model model) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) {
            return "redirect:/hospital/login";
        }

        model.addAttribute("hospital", hospital);
        model.addAttribute("activeCount", bloodRequestService.getActiveCountByHospital(hospital));
        model.addAttribute("deliveringCount", bloodRequestService.getDeliveringCountByHospital(hospital));
        model.addAttribute("deliveredCount", bloodRequestService.getDeliveredCountByHospital(hospital));
        model.addAttribute("recentRequests", bloodRequestService.getRecentActiveRequestsByHospital(hospital));

        return "hospital/hospitalDashboard";
    }
}
