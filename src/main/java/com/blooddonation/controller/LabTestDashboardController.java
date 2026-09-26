package com.blooddonation.controller;

import com.blooddonation.util.Validate;
import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.LabTester;
import com.blooddonation.service.LabTestDashboardService;
import com.blooddonation.service.LabTestOperationsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Handles the lab tester dashboard, pending blood test screening and result submission.
 * Split out of the original LabTestController to keep this concern isolated.
 */
@Controller
@RequestMapping("/labtest")
public class LabTestDashboardController {

    @Autowired
    private LabTestDashboardService labTestDashboardService;

    @Autowired
    private LabTestOperationsService labTestOperationsService;

    @GetMapping({"/dashboard", ""})
    public String showDashboard(HttpSession session, Model model) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        long awaitingCount = labTestDashboardService.getAwaitingScreeningCount();
        long screenedTodayCount = labTestDashboardService.getScreenedTodayCount();
        long pathogenCount = labTestDashboardService.getPathogenDetectedCount();
        List<BloodBag> pendingBags = labTestOperationsService.getPendingBloodBags();

        model.addAttribute("tester", tester);
        model.addAttribute("awaitingCount", awaitingCount);
        model.addAttribute("screenedTodayCount", screenedTodayCount);
        model.addAttribute("pathogenCount", pathogenCount);
        model.addAttribute("pendingBags", pendingBags);

        return "labtest/labtestDashboard";
    }

    @GetMapping("/pending")
    public String showPendingTests(HttpSession session, Model model) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        List<BloodBag> pendingBags = labTestOperationsService.getPendingBloodBags();
        model.addAttribute("tester", tester);
        model.addAttribute("pendingBags", pendingBags);
        return "labtest/labtestPending";
    }

    @PostMapping("/submit-results")
    public String submitResults(@RequestParam("bagId") Integer bagId,
                                @RequestParam("hiv") String hiv,
                                @RequestParam("hepatitis") String hepatitis,
                                @RequestParam("syphilis") String syphilis,
                                @RequestParam("testOutcome") String testOutcome,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("labtester") == null) return "redirect:/labtest/login";

        String error = Validate.first(
                Validate.required("Blood bag", bagId),
                Validate.oneOf("HIV result", hiv, "Negative", "Positive"),
                Validate.oneOf("Hepatitis result", hepatitis, "Negative", "Positive"),
                Validate.oneOf("Syphilis result", syphilis, "Negative", "Positive"),
                Validate.oneOf("Test outcome", testOutcome, "Pass", "Fail")
        );
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/labtest/pending";
        }

        try {
            labTestOperationsService.submitTestResults(bagId, hiv, hepatitis, syphilis, testOutcome);
            redirectAttributes.addFlashAttribute("successMessage", "Test results successfully submitted and inventory updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/labtest/pending";
    }
}
