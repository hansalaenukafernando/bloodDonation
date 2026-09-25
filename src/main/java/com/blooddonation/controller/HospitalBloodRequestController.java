package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.BloodRequest;
import com.blooddonation.entity.Hospital;
import com.blooddonation.service.HospitalBloodRequestService;
import com.blooddonation.util.Validate;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hospital/requests")
public class HospitalBloodRequestController {

    @Autowired
    private HospitalBloodRequestService bloodRequestService;

    // ප්‍රධාන Blood Requests පිටුව (Delivered නොවූ Active ඉල්ලීම් පමණක් පෙන්වයි)
    @GetMapping
    public String showBloodRequests(HttpSession session, Model model) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        // getRequestsByHospital වෙනුවට getActiveRequestsByHospital භාවිතා කර ඇත
        List<BloodRequest> requests = bloodRequestService.getActiveRequestsByHospital(hospital);
        model.addAttribute("hospital", hospital);
        model.addAttribute("requests", requests);
        return "hospital/hospitalBloodRequests";
    }

    // අලුතින් එකතු කළ යුතු: Request History පිටුව (Delivered වූ ඉල්ලීම් පමණක් පෙන්වයි)
    @GetMapping("/history")
    public String showRequestHistory(HttpSession session, Model model) {
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        List<BloodRequest> historyRequests = bloodRequestService.getDeliveredHistoryByHospital(hospital);
        model.addAttribute("hospital", hospital);
        model.addAttribute("historyRequests", historyRequests);
        return "hospital/hospitalRequestHistory";
    }

    @PostMapping("/add")
    public String createRequest(@RequestParam("priority") String priority,
                                @RequestParam("bloodGroup") String bloodGroup,
                                @RequestParam("quantity") Integer quantity,
                                @RequestParam("ward") String ward,
                                @RequestParam("patientName") String patientName,
                                @RequestParam("patientAge") Integer patientAge,
                                @RequestParam("patientGender") String patientGender,
                                @RequestParam(value = "patientIdNumber", required = false) String patientIdNumber,
                                @RequestParam(value = "notes", required = false) String notes,
                                HttpSession session, RedirectAttributes ra) {

        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        String error = Validate.first(
                Validate.oneOf("Priority", priority, "standard", "stat"),
                Validate.bloodGroup("Blood group", bloodGroup),
                Validate.number("Quantity", quantity, 1, 50),
                Validate.text("Ward", ward, 2, 100),
                Validate.name("Patient name", patientName),
                Validate.number("Patient age", patientAge, 0, 120),
                Validate.oneOf("Gender", patientGender, "Male", "Female", "Other"),
                Validate.optionalText("Patient ID number", patientIdNumber, 50),
                Validate.optionalText("Clinical notes", notes, 500)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/hospital/requests";
        }

        try {
            bloodRequestService.createBloodRequest(hospital, priority, bloodGroup, quantity, ward, patientName, patientAge, patientGender, patientIdNumber, notes);
            ra.addFlashAttribute("successMessage", "Blood request submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to submit request: " + e.getMessage());
        }
        return "redirect:/hospital/requests";
    }

    @PostMapping("/mark-delivered/{id}")
    public String markDelivered(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            bloodRequestService.markAsDelivered(id);
            ra.addFlashAttribute("successMessage", "Blood Delivery confirmed successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to confirm delivery: " + e.getMessage());
        }
        return "redirect:/hospital/requests";
    }

    // Request එක තාම "Pending" නම් විතරයි hospital එකට edit කරන්න දෙන්නේ.
    // Admin එක Approve/Reject කරාට පස්සේ service එකෙන්ම block වෙනවා.
    @PostMapping("/update")
    public String updateRequest(@RequestParam("requestId") Integer requestId,
                                @RequestParam("priority") String priority,
                                @RequestParam("bloodGroup") String bloodGroup,
                                @RequestParam("quantity") Integer quantity,
                                @RequestParam("ward") String ward,
                                @RequestParam("patientName") String patientName,
                                @RequestParam("patientAge") Integer patientAge,
                                @RequestParam("patientGender") String patientGender,
                                @RequestParam(value = "patientIdNumber", required = false) String patientIdNumber,
                                @RequestParam(value = "notes", required = false) String notes,
                                HttpSession session, RedirectAttributes ra) {

        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        String error = Validate.first(
                Validate.required("Request", requestId),
                Validate.oneOf("Priority", priority, "standard", "stat"),
                Validate.bloodGroup("Blood group", bloodGroup),
                Validate.number("Quantity", quantity, 1, 50),
                Validate.text("Ward", ward, 2, 100),
                Validate.name("Patient name", patientName),
                Validate.number("Patient age", patientAge, 0, 120),
                Validate.oneOf("Gender", patientGender, "Male", "Female", "Other"),
                Validate.optionalText("Patient ID number", patientIdNumber, 50),
                Validate.optionalText("Clinical notes", notes, 500)
        );
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
            return "redirect:/hospital/requests";
        }

        try {
            bloodRequestService.updateHospitalRequest(requestId, hospital, priority, bloodGroup, quantity, ward,
                    patientName, patientAge, patientGender, patientIdNumber, notes);
            ra.addFlashAttribute("successMessage", "Blood request updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/hospital/requests";
    }

    // Request එක තාම "Pending" නම් විතරයි hospital එකට delete කරන්න දෙන්නේ.
    @PostMapping("/delete")
    public String deleteRequest(@RequestParam("requestId") Integer requestId,
                                HttpSession session, RedirectAttributes ra) {

        Hospital hospital = (Hospital) session.getAttribute("hospital");
        if (hospital == null) return "redirect:/hospital/login";

        try {
            bloodRequestService.deleteHospitalRequest(requestId, hospital);
            ra.addFlashAttribute("successMessage", "Blood request deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/hospital/requests";
    }

}