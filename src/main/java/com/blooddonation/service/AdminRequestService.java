package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.DonorRequest;
import com.blooddonation.repository.AdminRequestRepository;
import com.blooddonation.repository.BloodBagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminRequestService {

    @Autowired
    private AdminRequestRepository requestRepository;

    @Autowired
    private BloodBagRepository bloodBagRepository;

    public List<DonorRequest> getAllRequests() {
        // Active management page: only requests still in progress
        return requestRepository.findByStatusNotInOrderByPreferredDateDesc(List.of("Completed", "Cancelled"));
    }

    public List<DonorRequest> getRequestHistory() {
        // Donation Requests History page: finalized requests (completed or rejected/cancelled)
        return requestRepository.findByStatusInOrderByPreferredDateDesc(List.of("Completed", "Cancelled"));
    }

    public void updateRequestStatus(Integer requestId, String status) throws Exception {
        DonorRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Donation request not found"));

        if ("Complete".equals(status)) {
            LocalDate today = LocalDate.now();
            if (request.getPreferredDate().isAfter(today)) {
                throw new Exception("Cannot complete this request because the preferred date has not arrived yet!");
            }

            BloodBag bloodBag = new BloodBag();

            // setDonorId වෙනුවට setDonor භාවිතා කරන්න
            if (request.getDonor() != null) {
                bloodBag.setDonor(request.getDonor());
            }

            bloodBag.setBloodGroup(request.getDonor().getBloodGroup());
            bloodBag.setCollectionDate(today);
            bloodBag.setExpiryDate(today.plusDays(42));
            bloodBag.setStatus("Untested");

            bloodBagRepository.save(bloodBag);
            request.setStatus("Completed");

        } else if ("Confirmed".equals(status)) {
            request.setStatus("Confirmed");
        } else if ("Cancelled".equals(status)) {
            request.setStatus("Cancelled");
        }

        requestRepository.save(request);
    }
}