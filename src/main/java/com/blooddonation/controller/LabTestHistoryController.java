package com.blooddonation.controller;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.LabTester;
import com.blooddonation.service.LabTestOperationsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Handles the tested blood history view for lab testers.
 * Split out of the original LabTestController to keep this concern isolated.
 */
@Controller
@RequestMapping("/labtest")
public class LabTestHistoryController {

    @Autowired
    private LabTestOperationsService labTestOperationsService;

    @GetMapping("/history")
    public String showTestHistory(HttpSession session, Model model) {
        LabTester tester = (LabTester) session.getAttribute("labtester");
        if (tester == null) return "redirect:/labtest/login";

        List<BloodBag> historyBags = labTestOperationsService.getTestedBloodHistory();
        model.addAttribute("tester", tester);
        model.addAttribute("historyBags", historyBags);
        return "labtest/labtestHistory";
    }
}
