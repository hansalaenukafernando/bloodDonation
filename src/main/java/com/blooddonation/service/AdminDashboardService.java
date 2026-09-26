package com.blooddonation.service;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.repository.BloodBagRepository;
import com.blooddonation.repository.CampRequestRepository;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.HospitalBloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    // Standard 8 blood groups tracked across the inventory pages (adminBloodInventory, etc.)
    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    // Below this many available units, a blood group is flagged as a shortage on the dashboard.
    private static final int LOW_STOCK_THRESHOLD = 5;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalBloodRequestRepository bloodRequestRepository;

    @Autowired
    private BloodBagRepository bloodBagRepository;

    @Autowired
    private CampRequestRepository campRequestRepository;

    // Total Donors ගණන
    public long getTotalDonorsCount() {
        return donorRepository.count();
    }

    // Registered Hospitals ගණන
    public long getTotalHospitalsCount() {
        return hospitalRepository.count();
    }

    // Active Deliveries ගණන (Delivering තත්ත්වයේ ඇති ඒවා)
    public long getActiveDeliveriesCount() {
        return bloodRequestRepository.findByStatus("Delivering").size();
    }

    // Pending Tests ගණන (තවම test කරලා නැති/Untested blood bags) — same status used by
    // AdminBloodTestController's "/admin/blood-tests/pending" page, so the dashboard count matches it.
    public long getPendingTestsCount() {
        return bloodBagRepository.findByStatus("Untested").size();
    }

    /**
     * Live system alerts for the dashboard: low blood-group stock and expired bags
     * still marked "Available" (need to be pulled/discarded). Replaces what used to
     * be two hardcoded fake alert strings in the template.
     */
    public List<String> getSystemAlerts() {
        List<String> alerts = new ArrayList<>();

        for (String group : BLOOD_GROUPS) {
            Integer count = bloodBagRepository.countAvailableByBloodGroup(group);
            int stock = count != null ? count : 0;
            if (stock < LOW_STOCK_THRESHOLD) {
                alerts.add("Critical Shortage: " + group + " inventory at " + stock + " unit(s), below the safe threshold.");
            }
        }

        int expiredCount = bloodBagRepository.findExpiredAvailableBags(LocalDate.now()).size();
        if (expiredCount > 0) {
            alerts.add(expiredCount + " blood bag(s) have passed their expiry date and still need to be discarded.");
        }

        return alerts;
    }

    /**
     * Camp requests still sitting in "Pending" status, awaiting admin review on the
     * Camp Requests page. Replaces the hardcoded fake "Mercy General Hospital" approval card.
     */
    public List<CampRequest> getPendingApprovals() {
        return campRequestRepository.findByStatus("Pending");
    }
}