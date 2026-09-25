package com.blooddonation.service;

import com.blooddonation.entity.Donor;
import com.blooddonation.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    public Donor registerDonor(Donor donor) throws Exception {
        if (donorRepository.existsByNic(donor.getNic())) {
            throw new Exception("A donor with this NIC is already registered.");
        }
        if (donorRepository.existsByEmail(donor.getEmail())) {
            throw new Exception("A donor with this email address is already registered.");
        }
        return donorRepository.save(donor);
    }

    public Donor authenticateDonor(String email, String password) throws Exception {
        Optional<Donor> donorOpt = donorRepository.findByEmail(email);

        if (donorOpt.isPresent()) {
            Donor donor = donorOpt.get();

            // Check if account is deactivated by admin
            if ("Deactivated".equalsIgnoreCase(donor.getStatus())) {
                throw new Exception("Your account has been deactivated by the administrator. Please contact our hotline at +94 11 234 5678 for assistance.");
            }

            // Check if passwords match
            if (donor.getPasswordHash().equals(password)) {
                return donor;
            } else {
                throw new Exception("Invalid password. Please try again.");
            }
        } else {
            throw new Exception("No account found with this email address.");
        }
    }

    public Donor updateDonorProfile(Integer donorId, String name, String email, String contactNumber, String address) throws Exception {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new Exception("Donor not found."));

        Optional<Donor> existingEmail = donorRepository.findByEmail(email);
        if (existingEmail.isPresent() && !existingEmail.get().getDonorId().equals(donorId)) {
            throw new Exception("This email address is already in use by another account.");
        }

        donor.setName(name);
        donor.setEmail(email);
        donor.setContactNumber(contactNumber);
        donor.setAddress(address);

        return donorRepository.save(donor);
    }

    public void updatePassword(Integer donorId, String currentPassword, String newPassword) throws Exception {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new Exception("Donor not found."));

        if (!donor.getPasswordHash().equals(currentPassword)) {
            throw new Exception("Incorrect current password.");
        }

        donor.setPasswordHash(newPassword);
        donorRepository.save(donor);
    }
}