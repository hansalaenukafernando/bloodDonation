package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.Donor;
import com.blooddonation.entity.DonorRequest;
import com.blooddonation.entity.Location;
import com.blooddonation.repository.LocationRepository;
import com.blooddonation.service.DonorRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles scheduling, updating and cancelling donation requests.
 * Split out of the original DonorController to keep this concern isolated.
 */
@Controller
@RequestMapping("/donor/requests")
public class DonorRequestController {

    @Autowired
    private DonorRequestService requestService;

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public String showSchedulePage(HttpSession session, Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        List<DonorRequest> requests = requestService.getActiveRequestsByDonor(loggedInDonor.getDonorId());
        List<Location> activeLocations = locationRepository.findByIsActiveTrue();

        model.addAttribute("donor", loggedInDonor);
        model.addAttribute("requests", requests);
        model.addAttribute("locations", activeLocations);
        return "donor/donorRequests";
    }

    @PostMapping("/add")
    public String addRequest(@RequestParam("locationId") Integer locationId,
                             @RequestParam("donationDate") String donationDate,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        LocalDate preferredDate = Validate.parseDate(donationDate);
        String error = Validate.first(
                Validate.required("Donation center", locationId),
                Validate.futureDate("Donation date", preferredDate)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/donor/requests";
        }

        try {
            Location selectedLocation = locationRepository.findById(locationId).orElseThrow();
            DonorRequest request = new DonorRequest();
            request.setDonor(loggedInDonor);
            request.setLocation(selectedLocation);
            request.setPreferredDate(preferredDate);

            requestService.saveRequest(request);
            redirectAttributes.addFlashAttribute("successMessage", "Donation request scheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/donor/requests";
    }

    @PostMapping("/update")
    public String updateRequest(@RequestParam("requestId") Integer requestId,
                                @RequestParam("locationId") Integer locationId,
                                @RequestParam("donationDate") String donationDate,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("donor") == null) return "redirect:/donor/login";

        LocalDate preferredDate = Validate.parseDate(donationDate);
        String error = Validate.first(
                Validate.required("Request", requestId),
                Validate.required("Donation center", locationId),
                Validate.futureDate("Donation date", preferredDate)
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/donor/requests";
        }

        try {
            Location selectedLocation = locationRepository.findById(locationId).orElseThrow();
            DonorRequest request = new DonorRequest();
            request.setLocation(selectedLocation);
            request.setPreferredDate(preferredDate);

            requestService.updateRequest(requestId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Donation request updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/donor/requests";
    }

    @PostMapping("/delete")
    public String deleteRequest(@RequestParam("requestId") Integer requestId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("donor") == null) return "redirect:/donor/login";

        try {
            requestService.deleteRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage", "Donation request cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel request.");
        }
        return "redirect:/donor/requests";
    }
}
