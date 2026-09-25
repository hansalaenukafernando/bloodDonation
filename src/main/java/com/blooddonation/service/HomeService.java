package com.blooddonation.service;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.repository.BloodBagRepository;
import com.blooddonation.repository.CampRequestRepository;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HomeService {

    // Order the 8 standard blood groups are displayed on the homepage
    private static final List<String> BLOOD_GROUPS = Arrays.asList(
            "O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"
    );

    // Stock-level thresholds (available units) used to label each group's urgency.
    // Tune these to match real clinical thresholds if/when available.
    private static final int CRITICAL_MAX = 0;  // 0 units  -> Critical
    private static final int LOW_MAX = 5;        // 1-5      -> Low
    private static final int ADEQUATE_MAX = 15;  // 6-15     -> Adequate
    // 16+ units -> Optimal

    @Autowired
    private BloodBagRepository bloodBagRepository;

    @Autowired
    private CampRequestRepository campRequestRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DonorRepository donorRepository;

    public List<BloodStockItem> getBloodStockStatus() {
        List<BloodStockItem> stock = new ArrayList<>();
        for (String group : BLOOD_GROUPS) {
            Integer countObj = bloodBagRepository.countAvailableByBloodGroup(group);
            int count = (countObj != null) ? countObj : 0;
            stock.add(new BloodStockItem(group, count, labelFor(count)));
        }
        return stock;
    }

    private String labelFor(int count) {
        if (count <= CRITICAL_MAX) return "Critical";
        if (count <= LOW_MAX) return "Low";
        if (count <= ADEQUATE_MAX) return "Adequate";
        return "Optimal";
    }

    // Blood groups currently at Critical or Low stock — used for the emergency banner
    public List<String> getCriticalGroups() {
        List<String> critical = new ArrayList<>();
        for (BloodStockItem item : getBloodStockStatus()) {
            if (item.isNeedsAttention()) {
                critical.add(item.getBloodGroup());
            }
        }
        return critical;
    }

    // Upcoming confirmed/approved mobile campaigns, soonest first, capped for the homepage table
    public List<CampRequest> getUpcomingCamps(int limit) {
        List<CampRequest> upcoming = campRequestRepository.findUpcomingConfirmedCamps(LocalDate.now());
        if (upcoming.size() > limit) {
            return upcoming.subList(0, limit);
        }
        return upcoming;
    }

    public long getActiveLocationCount() {
        return locationRepository.countByIsActiveTrue();
    }

    public long getRegisteredDonorCount() {
        return donorRepository.countByStatus("Active");
    }
}