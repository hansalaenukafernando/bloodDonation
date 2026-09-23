package com.blooddonation.controller;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.service.HospitalBloodRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/deliveries")
public class AdminDeliveryController {

    @Autowired
    private HospitalBloodRequestService requestService;

    // Delivery Management Page එක පෙන්වීම
    @GetMapping
    public String showDeliveries(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("approvedRequests", requestService.getApprovedRequests());
        model.addAttribute("activeDeliveries", requestService.getAllDeliveries());
        // දැනට active (Delivering) delivery එකක යෙදවිලා නැති drivers/cool boxes විතරයි පෙන්වන්නේ
        model.addAttribute("drivers", requestService.getAvailableDrivers());
        model.addAttribute("coolBoxes", requestService.getAvailableCoolBoxes());
        model.addAttribute("bloodGroups", List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));

        return "admin/adminDeliveries";
    }

    // Delivery History Page එක පෙන්වීම (Delivered වූ ඒවා විතරයි)
    @GetMapping("/history")
    public String showDeliveryHistory(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("deliveryHistory", requestService.getDeliveryHistory());
        return "admin/adminDeliveryHistory";
    }

    // Driver, Cool Box සහ Blood Group Assign කර Delivery ආරම්භ කිරීම
    @PostMapping("/dispatch/{id}")
    public String dispatchBlood(HttpSession session,
                                @PathVariable("id") Integer requestId,
                                @RequestParam("driverId") Integer driverId,
                                @RequestParam("coolBoxId") Integer coolBoxId,
                                @RequestParam("bloodGroupUsed") String bloodGroupUsed,
                                RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        try {
            requestService.assignDeliveryAndDispatch(requestId, driverId, coolBoxId, bloodGroupUsed);
            ra.addFlashAttribute("successMessage", "Blood dispatched successfully! Status: Delivering");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to dispatch: " + e.getMessage());
        }
        return "redirect:/admin/deliveries";
    }

}