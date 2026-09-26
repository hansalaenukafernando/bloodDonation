package com.blooddonation.controller;

import com.blooddonation.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("bloodStock", homeService.getBloodStockStatus());
        model.addAttribute("criticalGroups", homeService.getCriticalGroups());
        model.addAttribute("upcomingCamps", homeService.getUpcomingCamps(5));
        model.addAttribute("activeLocationCount", homeService.getActiveLocationCount());
        model.addAttribute("registeredDonorCount", homeService.getRegisteredDonorCount());
        return "home";
    }
}