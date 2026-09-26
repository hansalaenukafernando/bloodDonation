package com.blooddonation.service;

import com.blooddonation.entity.Hospital;
import com.blooddonation.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public void registerHospital(Hospital hospital) throws Exception {
        if (hospitalRepository.findByEmail(hospital.getEmail()).isPresent()) {
            throw new Exception("An account with this email address already exists.");
        }
        if (hospitalRepository.findByLicenseNumber(hospital.getLicenseNumber()).isPresent()) {
            throw new Exception("A facility with this Clinical License ID is already registered.");
        }

        hospital.setStatus("Active");
        hospitalRepository.save(hospital);
    }

    public Hospital authenticateHospital(String email, String password) throws Exception {
        Hospital hospital = hospitalRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("No hospital found with this email address."));

        if ("Deactivated".equalsIgnoreCase(hospital.getStatus())) {
            throw new Exception("This hospital account has been deactivated by the administrator.");
        }

        if (hospital.getPasswordHash().equals(password)) {
            return hospital;
        } else {
            throw new Exception("Invalid password. Please try again.");
        }
    }

    public Hospital updateHospitalProfile(Integer hospitalId, String name, String licenseNumber, String email, String contactNumber) throws Exception {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new Exception("Hospital not found."));

        Optional<Hospital> existingEmail = hospitalRepository.findByEmail(email);
        if (existingEmail.isPresent() && !existingEmail.get().getHospitalId().equals(hospitalId)) {
            throw new Exception("This email address is already in use by another account.");
        }

        Optional<Hospital> existingLicense = hospitalRepository.findByLicenseNumber(licenseNumber);
        if (existingLicense.isPresent() && !existingLicense.get().getHospitalId().equals(hospitalId)) {
            throw new Exception("This Clinical License ID is already registered by another facility.");
        }

        hospital.setName(name);
        hospital.setLicenseNumber(licenseNumber);
        hospital.setEmail(email);
        hospital.setContactNumber(contactNumber);

        return hospitalRepository.save(hospital);
    }

    public void updatePassword(Integer hospitalId, String currentPassword, String newPassword) throws Exception {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new Exception("Hospital not found."));

        if (!hospital.getPasswordHash().equals(currentPassword)) {
            throw new Exception("Incorrect current password.");
        }

        hospital.setPasswordHash(newPassword);
        hospitalRepository.save(hospital);
    }
}