package com.blooddonation.service;

import com.blooddonation.entity.Hospital;
import com.blooddonation.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminHospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public void updateHospital(Integer hospitalId, String name, String licenseNumber,
                               String email, String contactNumber) throws Exception {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new Exception("Hospital not found"));

        Optional<Hospital> existingEmail = hospitalRepository.findByEmail(email);
        if (existingEmail.isPresent() && !existingEmail.get().getHospitalId().equals(hospitalId)) {
            throw new Exception("This email address is already in use by another hospital.");
        }

        Optional<Hospital> existingLicense = hospitalRepository.findByLicenseNumber(licenseNumber);
        if (existingLicense.isPresent() && !existingLicense.get().getHospitalId().equals(hospitalId)) {
            throw new Exception("This Clinical License ID is already registered by another hospital.");
        }

        hospital.setName(name);
        hospital.setLicenseNumber(licenseNumber);
        hospital.setEmail(email);
        hospital.setContactNumber(contactNumber);

        hospitalRepository.save(hospital);
    }

    public void updateHospitalStatus(Integer hospitalId, String status) throws Exception {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new Exception("Hospital not found"));

        hospital.setStatus(status);
        hospitalRepository.save(hospital);
    }
}