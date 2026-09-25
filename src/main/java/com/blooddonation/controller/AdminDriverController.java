package com.blooddonation.controller;

import com.blooddonation.entity.Driver;
import com.blooddonation.service.AdminDriverService;
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
@RequestMapping("/admin/drivers")
public class AdminDriverController {

    @Autowired
    private AdminDriverService adminDriverService;

    @GetMapping
    public String showDriversManagement(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<Driver> drivers = adminDriverService.getAllDrivers();
        model.addAttribute("drivers", drivers);
        return "admin/adminDrivers";
    }

    @PostMapping("/add")
    public String addDriver(@RequestParam("name") String name,
                            @RequestParam("phone") String phone,
                            @RequestParam("licenseNumber") String licenseNumber,
                            HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDriverService.addDriver(name, phone, licenseNumber);
            ra.addFlashAttribute("successMessage", "Driver added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/drivers";
    }

    @PostMapping("/update")
    public String updateDriver(@RequestParam("driverId") Integer driverId,
                               @RequestParam("name") String name,
                               @RequestParam("phone") String phone,
                               @RequestParam("licenseNumber") String licenseNumber,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDriverService.updateDriver(driverId, name, phone, licenseNumber);
            ra.addFlashAttribute("successMessage", "Driver details updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/drivers";
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam("driverId") Integer driverId,
                               @RequestParam("status") String status,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDriverService.updateDriverStatus(driverId, status);
            ra.addFlashAttribute("successMessage", "Driver status changed to " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update driver status.");
        }
        return "redirect:/admin/drivers";
    }

    @PostMapping("/delete")
    public String deleteDriver(@RequestParam("driverId") Integer driverId,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            adminDriverService.deleteDriver(driverId);
            ra.addFlashAttribute("successMessage", "Driver deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to delete driver.");
        }
        return "redirect:/admin/drivers";
    }
}