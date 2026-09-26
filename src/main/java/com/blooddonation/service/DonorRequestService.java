package com.blooddonation.service;

import com.blooddonation.entity.DonorRequest;
import com.blooddonation.repository.DonorRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class DonorRequestService {

    @Autowired
    private DonorRequestRepository requestRepository;

    public void saveRequest(DonorRequest newRequest) throws Exception {
        if (!newRequest.getPreferredDate().isAfter(LocalDate.now())) {
            throw new Exception("Preferred date must be tomorrow or a later date. Today and past dates are not allowed.");
        }

        Integer donorId = newRequest.getDonor().getDonorId();

        // Fetch all existing requests for this donor
        List<DonorRequest> existingRequests = requestRepository.findByDonor_DonorIdOrderByPreferredDateDesc(donorId);

        for (DonorRequest existing : existingRequests) {
            // Skip checking against itself if it's an update (though this is for save)
            long daysBetween = ChronoUnit.DAYS.between(existing.getPreferredDate(), newRequest.getPreferredDate());

            // If the new date is within 14 days before or after any existing request
            if (Math.abs(daysBetween) < 14) {
                throw new Exception("You must maintain at least a 14-day gap between your donation appointments. Conflict found with date: "
                        + existing.getPreferredDate());
            }
        }

        requestRepository.save(newRequest);
    }

    public void updateRequest(Integer requestId, DonorRequest updatedData) throws Exception {
        if (!updatedData.getPreferredDate().isAfter(LocalDate.now())) {
            throw new Exception("Preferred date must be tomorrow or a later date. Today and past dates are not allowed.");
        }

        DonorRequest existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        existingRequest.setLocation(updatedData.getLocation());
        existingRequest.setPreferredDate(updatedData.getPreferredDate());

        requestRepository.save(existingRequest);
    }

    public void deleteRequest(Integer requestId) {
        requestRepository.deleteById(requestId);
    }

    public List<DonorRequest> getRequestsByDonor(Integer donorId) {
        return requestRepository.findByDonor_DonorIdOrderByPreferredDateDesc(donorId);
    }

    // Used by the "Schedule a Donation" page — only appointments that are still active (not yet finalized)
    public List<DonorRequest> getActiveRequestsByDonor(Integer donorId) {
        return requestRepository.findByDonor_DonorIdAndStatusInOrderByPreferredDateDesc(donorId, List.of("Pending", "Confirmed"));
    }

    public long getTotalDonationsCount(Integer donorId) {
        return requestRepository.countByDonor_DonorId(donorId);
    }

    public LocalDate getNextEligibleDate(Integer donorId) {
        Optional<DonorRequest> lastRequestOpt = requestRepository.findTopByDonor_DonorIdOrderByPreferredDateDesc(donorId);
        if (lastRequestOpt.isPresent()) {
            LocalDate lastDate = lastRequestOpt.get().getPreferredDate();
            return lastDate.plusDays(14);
        }
        return LocalDate.now();
    }

    public boolean isEligibleToDonate(Integer donorId) {
        LocalDate nextEligible = getNextEligibleDate(donorId);
        return !LocalDate.now().isBefore(nextEligible);
    }
}