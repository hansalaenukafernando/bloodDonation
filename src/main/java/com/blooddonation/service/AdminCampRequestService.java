package com.blooddonation.service;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.repository.CampRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminCampRequestService {

    @Autowired
    private CampRequestRepository campRequestRepository;

    public List<CampRequest> getAllCampRequests() {
        // Active management page: only requests still in progress
        return campRequestRepository.findByStatusNotInOrderByPreferredDateDesc(List.of("Completed", "Cancelled"));
    }

    public List<CampRequest> getCampRequestHistory() {
        // Camp Requests History page: finalized requests (completed or cancelled/rejected)
        return campRequestRepository.findByStatusInOrderByPreferredDateDesc(List.of("Completed", "Cancelled"));
    }

    public void updateCampStatus(Integer requestId, String status) throws Exception {
        CampRequest req = campRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Camp request not found"));

        // If trying to complete, ensure the preferred date has arrived or passed
        if ("Completed".equalsIgnoreCase(status)) {
            if (req.getPreferredDate() != null && req.getPreferredDate().isAfter(LocalDate.now())) {
                throw new Exception("Cannot complete this camp because the scheduled date has not arrived yet.");
            }
        }

        req.setStatus(status);
        campRequestRepository.save(req);
    }
}