package com.blooddonation.service;

import com.blooddonation.entity.Donor;
import com.blooddonation.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDonorService {

    @Autowired
    private DonorRepository donorRepository;

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public void updateDonorByAdmin(Integer donorId, String name, String email, String contactNumber, String bloodGroup, String address) throws Exception {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new Exception("Donor not found"));

        donor.setName(name);
        donor.setEmail(email);
        donor.setContactNumber(contactNumber);
        donor.setBloodGroup(bloodGroup);
        donor.setAddress(address);

        donorRepository.save(donor);
    }

    public void updateDonorStatus(Integer donorId, String status) throws Exception {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new Exception("Donor not found"));

        donor.setStatus(status);
        donorRepository.save(donor);
    }
}