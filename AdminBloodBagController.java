package com.blooddonation.controller;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.service.AdminBloodBagService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/blood-bags")
public class AdminBloodBagController {

    @Autowired
    private AdminBloodBagService adminBloodBagService;

    @GetMapping
    public String showBloodBagsManagement(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<BloodBag> bloodBags = adminBloodBagService.getAllBloodBags();
        model.addAttribute("bloodBags", bloodBags);
        return "admin/adminBloodBags";
    }
}