package com.blooddonation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/role")
    public String viewRolePage(Model model) {
        model.addAttribute("activePage", "role");
        return "role";
    }

    @GetMapping("/services")
    public String viewServices(Model model) {
        model.addAttribute("activePage", "services");
        return "services";
    }

    @GetMapping("/events")
    public String viewEvents(Model model) {
        model.addAttribute("activePage", "events");
        return "events";
    }

    @GetMapping("/contactUs")
    public String viewContactUs(Model model) {
        model.addAttribute("activePage", "contactUs");
        return "contactUs";
    }

    @GetMapping("/forDonors")
    public String viewForDonors(Model model) {
        model.addAttribute("activePage", "forDonors");
        return "forDonors";
    }
}