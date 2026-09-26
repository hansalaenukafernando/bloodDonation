package com.blooddonation.service;

import com.blooddonation.entity.LabTester;
import com.blooddonation.repository.LabTesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLabTesterService {

    @Autowired
    private LabTesterRepository labTesterRepository;

    public List<LabTester> getAllLabTesters() {
        return labTesterRepository.findAll();
    }

    public void addLabTester(String name, String email, String password) throws Exception {
        if (labTesterRepository.findByEmail(email).isPresent()) {
            throw new Exception("A lab tester with this email already exists!");
        }

        LabTester tester = new LabTester();
        tester.setName(name);
        tester.setEmail(email);
        tester.setPasswordHash(password);
        tester.setStatus("Active");

        labTesterRepository.save(tester);
    }

    public void updateLabTester(Integer testerId, String name, String email) throws Exception {
        LabTester tester = labTesterRepository.findById(testerId)
                .orElseThrow(() -> new Exception("Lab tester not found"));

        tester.setName(name);
        tester.setEmail(email);

        labTesterRepository.save(tester);
    }

    public void updateLabTesterStatus(Integer testerId, String status) throws Exception {
        LabTester tester = labTesterRepository.findById(testerId)
                .orElseThrow(() -> new Exception("Lab tester not found"));

        tester.setStatus(status);
        labTesterRepository.save(tester);
    }
}