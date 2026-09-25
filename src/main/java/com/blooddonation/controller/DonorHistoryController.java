package com.blooddonation.controller;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.Donor;
import com.blooddonation.entity.DonorRequest;
import com.blooddonation.repository.BloodBagRepository;
import com.blooddonation.repository.DonorRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the donor donation history view.
 * Split out of the original DonorController to keep this concern isolated.
 */
@Controller
@RequestMapping("/donor")
public class DonorHistoryController {

    @Autowired
    private DonorRequestRepository donorRequestRepository;

    @Autowired
    private BloodBagRepository bloodBagRepository;

    @GetMapping("/history")
    public String showHistoryPage(HttpSession session, Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) return "redirect:/donor/login";

        Integer donorId = loggedInDonor.getDonorId();

        // History page shows every appointment the donor has ever made, regardless of
        // status (Pending / Confirmed / Completed / Cancelled), so nothing gets hidden.
        List<DonorRequest> historyList = donorRequestRepository.findByDonor_DonorIdOrderByPreferredDateDesc(donorId);

        // Count only successful/completed donations for the summary card
        long successfulCount = historyList.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.getStatus()))
                .count();

        // Match each completed appointment to the blood bag collected that day, so the real
        // lab (HIV / Hepatitis / Syphilis) results can be shown instead of placeholder vitals.
        // There's no direct FK between donor_requests and blood_bags, so we match by donor + date.
        List<BloodBag> donorBags = bloodBagRepository.findByDonor_DonorId(donorId);
        Map<Integer, BloodBag> labResultsByRequestId = new HashMap<>();
        for (DonorRequest req : historyList) {
            for (BloodBag bag : donorBags) {
                if (bag.getCollectionDate() != null && bag.getCollectionDate().equals(req.getPreferredDate())) {
                    labResultsByRequestId.put(req.getRequestId(), bag);
                    break;
                }
            }
        }

        model.addAttribute("donor", loggedInDonor);
        model.addAttribute("historyList", historyList);
        model.addAttribute("successfulCount", successfulCount);
        model.addAttribute("labResultsByRequestId", labResultsByRequestId);

        return "donor/donorHistory";
    }
}
