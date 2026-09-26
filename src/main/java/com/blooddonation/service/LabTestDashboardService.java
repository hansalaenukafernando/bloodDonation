package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.repository.BloodBagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Computes dashboard metrics for the lab tester dashboard.
 * Split out of the original LabTestService to keep this concern isolated.
 */
@Service
public class LabTestDashboardService {

    @Autowired
    private BloodBagRepository bloodBagRepository;

    public long getAwaitingScreeningCount() {
        return bloodBagRepository.findByStatus("Untested").size();
    }

    public long getScreenedTodayCount() {
        LocalDate today = LocalDate.now();
        List<BloodBag> available = bloodBagRepository.findByStatus("Available");
        List<BloodBag> discarded = bloodBagRepository.findByStatus("Discarded");

        long count = 0;
        for (BloodBag b : available) {
            if (b.getTestedDate() != null && b.getTestedDate().equals(today)) count++;
        }
        for (BloodBag b : discarded) {
            if (b.getTestedDate() != null && b.getTestedDate().equals(today)) count++;
        }
        return count;
    }

    public long getPathogenDetectedCount() {
        return bloodBagRepository.findByStatus("Discarded").size();
    }
}
