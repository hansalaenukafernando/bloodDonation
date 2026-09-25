package com.blooddonation.controller;

import com.blooddonation.entity.Driver;
import com.blooddonation.service.AdminDriverService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String addDriver(@Valid @ModelAttribute("driver") Driver driver,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes ra) {

        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMessage", firstErrorMessage(result));
            return "redirect:/admin/drivers";
        }

        try {
            adminDriverService.addDriver(driver.getName(), driver.getPhone(), driver.getLicenseNumber());
            ra.addFlashAttribute("successMessage", "Driver added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/drivers";
    }

    @PostMapping("/update")
    public String updateDriver(@Valid @ModelAttribute("driver") Driver driver,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes ra) {

        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        if (driver.getDriverId() == null) {
            ra.addFlashAttribute("errorMessage", "Driver not found.");
            return "redirect:/admin/drivers";
        }

        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMessage", firstErrorMessage(result));
            return "redirect:/admin/drivers";
        }

        try {
            adminDriverService.updateDriver(driver.getDriverId(), driver.getName(),
                                            driver.getPhone(), driver.getLicenseNumber());
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

    // Takes the message of the first failed field so the toast can show it
    private String firstErrorMessage(BindingResult result) {
        FieldError fieldError = result.getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            return fieldError.getDefaultMessage();
        }
        return "Validation failed. Please check the details you entered.";
    }
}
