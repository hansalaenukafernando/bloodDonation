package com.blooddonation.service;

import com.blooddonation.entity.Admin;
import com.blooddonation.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Authenticates an admin based on username and password.
     */
    public Admin authenticateAdmin(String username, String password) throws Exception {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            // Checking password (Plain text comparison matching project convention)
            if (admin.getPasswordHash().equals(password)) {
                return admin;
            } else {
                throw new Exception("Invalid master password. Please try again.");
            }
        } else {
            throw new Exception("No admin account found with this username.");
        }
    }

    public void updateAdminPassword(String username, String newPassword) throws Exception {
        // Assuming you have AdminRepository injected
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Admin not found"));

        admin.setPasswordHash(newPassword);
        adminRepository.save(admin);
    }


}