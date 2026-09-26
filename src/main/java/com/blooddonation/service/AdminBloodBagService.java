package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.repository.BloodBagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBloodBagService {

    @Autowired
    private BloodBagRepository bloodBagRepository;

    public List<BloodBag> getAllBloodBags() {
        return bloodBagRepository.findAll();
    }
}