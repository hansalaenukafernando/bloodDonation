package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.CampRequest;
import com.blooddonation.entity.Organization;
import com.blooddonation.service.CampRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/organization/camps")
public class CampRequestController {

    @Autowired
    private CampRequestService campRequestService;

    @GetMapping
    public String showCampRequests(HttpSession session, Model model) {
        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        List<CampRequest> requests = campRequestService.getRequestsByOrganization(org);
        model.addAttribute("organization", org);
        model.addAttribute("requests", requests);
        return "organization/organizationRequestCamp";
    }

    @PostMapping("/add")
    public String addRequest(@RequestParam("campName") String campName,
                             @RequestParam("preferredDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
                             @RequestParam("expectedDonors") Integer expectedDonors,
                             @RequestParam("setupType") String setupType,
                             @RequestParam("locationDetails") String locationDetails,
                             @RequestParam(value = "specialNotes", required = false) String specialNotes,
                             HttpSession session, RedirectAttributes ra) {

        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        String error = Validate.first(
                Validate.title("Camp name", campName),
                Validate.futureDate("Preferred date", preferredDate),
                Validate.number("Expected donors", expectedDonors, 10, 5000),
                Validate.required("Setup type", setupType),
                Validate.text("Location details", locationDetails, 5, 500),
                Validate.optionalText("Special notes", specialNotes, 500)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/camps";
        }

        try {
            campRequestService.createRequest(org, campName, preferredDate, expectedDonors, setupType, locationDetails, specialNotes);
            ra.addFlashAttribute("successMessage", "Blood camp request submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organization/camps";
    }

    @PostMapping("/update")
    public String updateRequest(@RequestParam("requestId") Integer requestId,
                                @RequestParam("campName") String campName,
                                @RequestParam("preferredDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
                                @RequestParam("expectedDonors") Integer expectedDonors,
                                @RequestParam("setupType") String setupType,
                                @RequestParam("locationDetails") String locationDetails,
                                @RequestParam(value = "specialNotes", required = false) String specialNotes,
                                HttpSession session, RedirectAttributes ra) {

        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        String error = Validate.first(
                Validate.required("Request", requestId),
                Validate.title("Camp name", campName),
                Validate.futureDate("Preferred date", preferredDate),
                Validate.number("Expected donors", expectedDonors, 10, 5000),
                Validate.required("Setup type", setupType),
                Validate.text("Location details", locationDetails, 5, 500),
                Validate.optionalText("Special notes", specialNotes, 500)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/organization/camps";
        }

        try {
            campRequestService.updateRequest(requestId, org, campName, preferredDate, expectedDonors, setupType, locationDetails, specialNotes);
            ra.addFlashAttribute("successMessage", "Camp request updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organization/camps";
    }

    @PostMapping("/delete")
    public String deleteRequest(@RequestParam("requestId") Integer requestId,
                                HttpSession session, RedirectAttributes ra) {
        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        try {
            campRequestService.deleteRequest(requestId, org);
            ra.addFlashAttribute("successMessage", "Camp request deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/organization/camps";
    }

    @GetMapping("/history")
    public String showCampHistory(HttpSession session, Model model) {
        Organization org = (Organization) session.getAttribute("organization");
        if (org == null) return "redirect:/organization/login";

        List<CampRequest> historyRequests = campRequestService.getCampHistoryByOrganization(org);
        model.addAttribute("organization", org);
        model.addAttribute("historyRequests", historyRequests);
        return "organization/organizationCampHistory";
    }
}