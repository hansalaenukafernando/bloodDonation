package com.blooddonation.controller;

import com.blooddonation.entity.CoolBox;
import com.blooddonation.service.AdminCoolBoxService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cool-boxes")
public class AdminCoolBoxController {

    @Autowired
    private AdminCoolBoxService adminCoolBoxService;

    @GetMapping
    public String showCoolBoxes(@RequestParam(value = "keyword", required = false) String keyword,
                                HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        List<CoolBox> coolBoxes = adminCoolBoxService.getAllCoolBoxes(keyword);
        model.addAttribute("coolBoxes", coolBoxes);
        model.addAttribute("keyword", keyword);
        return "admin/adminCoolBoxes";
    }

    @PostMapping("/add")
    public String addBox(@RequestParam("boxCode") String boxCode,
                         @RequestParam("currentTemp") String currentTemp,
                         HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminCoolBoxService.addCoolBox(boxCode, currentTemp);
            ra.addFlashAttribute("successMessage", "Cool box registered successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/cool-boxes";
    }

    @PostMapping("/update")
    public String updateBox(@RequestParam("boxId") Integer boxId,
                            @RequestParam("boxCode") String boxCode,
                            @RequestParam("currentTemp") String currentTemp,
                            HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminCoolBoxService.updateCoolBox(boxId, boxCode, currentTemp);
            ra.addFlashAttribute("successMessage", "Cool box updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/cool-boxes";
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam("boxId") Integer boxId,
                               @RequestParam("status") String status,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminCoolBoxService.updateStatus(boxId, status);
            ra.addFlashAttribute("successMessage", "Cool box status changed to " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update status.");
        }
        return "redirect:/admin/cool-boxes";
    }

    @PostMapping("/delete")
    public String deleteBox(@RequestParam("boxId") Integer boxId,
                            HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminCoolBoxService.deleteCoolBox(boxId);
            ra.addFlashAttribute("successMessage", "Cool box deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to delete cool box.");
        }
        return "redirect:/admin/cool-boxes";
    }
}