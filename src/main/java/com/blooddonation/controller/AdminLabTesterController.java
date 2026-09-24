package com.blooddonation.controller;

import com.blooddonation.entity.LabTester;
import com.blooddonation.service.AdminLabTesterService;
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

@Controller
@RequestMapping("/admin/lab-testers")
public class AdminLabTesterController {

    @Autowired
    private AdminLabTesterService adminLabTesterService;

    @GetMapping
    public String showLabTesterManagement(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<LabTester> labTesters = adminLabTesterService.getAllLabTesters();
        model.addAttribute("labTesters", labTesters);
        return "admin/labTesterManagement";
    }

    @PostMapping("/add")
    public String addLabTester(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminLabTesterService.addLabTester(name, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "Lab Tester added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/lab-testers";
    }

    @PostMapping("/update")
    public String updateLabTester(@RequestParam("testerId") Integer testerId,
                                  @RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminLabTesterService.updateLabTester(testerId, name, email);
            redirectAttributes.addFlashAttribute("successMessage", "Lab Tester details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/lab-testers";
    }

    @PostMapping("/status")
    public String changeLabTesterStatus(@RequestParam("testerId") Integer testerId,
                                        @RequestParam("status") String status,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminLabTesterService.updateLabTesterStatus(testerId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Lab Tester status changed to " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change status.");
        }
        return "redirect:/admin/lab-testers";
    }
}