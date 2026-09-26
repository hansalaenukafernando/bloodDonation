package com.blooddonation.service;

import com.blooddonation.entity.LabTester;
import com.blooddonation.repository.LabTesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles lab tester authentication and profile management.
 * Split out of the original LabTestService to keep this concern isolated.
 */
@Service
public class LabTestAuthService {

    @Autowired
    private LabTesterRepository labTesterRepository;

    public LabTester authenticateLabTester(String email, String password) throws Exception {
        Optional<LabTester> testerOpt = labTesterRepository.findByEmail(email);

        if (testerOpt.isPresent()) {
            LabTester tester = testerOpt.get();

            if ("Deactivated".equalsIgnoreCase(tester.getStatus())) {
                throw new Exception("Your account has been deactivated by the administrator. Please contact our hotline at +94 11 234 5678 for assistance.");
            }

            if (tester.getPasswordHash().equals(password)) {
                return tester;
            } else {
                throw new Exception("Invalid password. Please try again.");
            }
        } else {
            throw new Exception("No account found with this email address.");
        }
    }

    public LabTester updateProfile(Integer testerId, String name, String email) throws Exception {
        LabTester tester = labTesterRepository.findById(testerId)
                .orElseThrow(() -> new Exception("Lab tester not found"));

        tester.setName(name);
        tester.setEmail(email);
        return labTesterRepository.save(tester);
    }

    public void updatePassword(Integer testerId, String currentPassword, String newPassword) throws Exception {
        LabTester tester = labTesterRepository.findById(testerId)
                .orElseThrow(() -> new Exception("Lab tester not found"));

        if (!tester.getPasswordHash().equals(currentPassword)) {
            throw new Exception("Current password is incorrect.");
        }

        tester.setPasswordHash(newPassword);
        labTesterRepository.save(tester);
    }
}
