package com.blooddonation.service;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.entity.Organization;
import com.blooddonation.repository.CampRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampRequestService {

    @Autowired
    private CampRequestRepository campRequestRepository;

    public List<CampRequest> getRequestsByOrganization(Organization organization) {
        // Only show in-progress requests (Pending / Confirmed) on the "Request a Blood Camp" page.
        // Completed/Cancelled requests are shown on the Camp History page instead.
        return campRequestRepository.findByOrganizationAndStatusInOrderByPreferredDateDesc(
                organization, java.util.List.of("Pending", "Confirmed"));
    }

    public List<CampRequest> getCampHistoryByOrganization(Organization organization) {
        // Only show finalized requests (Completed / Cancelled) on the Camp History page.
        // Pending/Confirmed requests are shown on the Request a Blood Camp page instead.
        return campRequestRepository.findByOrganizationAndStatusInOrderByPreferredDateDesc(
                organization, java.util.List.of("Completed", "Cancelled"));
    }

    public void createRequest(Organization organization, String campName, LocalDate preferredDate,
                              Integer expectedDonors, String setupType,
                              String locationDetails, String specialNotes) throws Exception {
        if (!preferredDate.isAfter(LocalDate.now())) {
            throw new Exception("Preferred date must be tomorrow or a later date. Today and past dates are not allowed.");
        }

        CampRequest req = new CampRequest();
        req.setOrganization(organization);
        req.setCampName(campName);
        req.setPreferredDate(preferredDate);
        req.setExpectedDonors(expectedDonors);
        req.setSetupType(setupType);
        req.setLocationDetails(locationDetails);
        req.setSpecialNotes(specialNotes);
        req.setStatus("Pending");

        campRequestRepository.save(req);
    }

    public void updateRequest(Integer requestId, Organization organization, String campName, LocalDate preferredDate,
                              Integer expectedDonors, String setupType,
                              String locationDetails, String specialNotes) throws Exception {

        if (!preferredDate.isAfter(LocalDate.now())) {
            throw new Exception("Preferred date must be tomorrow or a later date. Today and past dates are not allowed.");
        }

        CampRequest req = campRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        if (!req.getOrganization().getOrgId().equals(organization.getOrgId())) {
            throw new Exception("Unauthorized action!");
        }

        if (!"Pending".equalsIgnoreCase(req.getStatus())) {
            throw new Exception("Cannot update request because it is no longer pending.");
        }

        req.setCampName(campName);
        req.setPreferredDate(preferredDate);
        req.setExpectedDonors(expectedDonors);
        req.setSetupType(setupType);
        req.setLocationDetails(locationDetails);
        req.setSpecialNotes(specialNotes);

        campRequestRepository.save(req);
    }

    public void deleteRequest(Integer requestId, Organization organization) throws Exception {
        CampRequest req = campRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        if (!req.getOrganization().getOrgId().equals(organization.getOrgId())) {
            throw new Exception("Unauthorized action!");
        }

        if (!"Pending".equalsIgnoreCase(req.getStatus())) {
            throw new Exception("Cannot delete request because it is no longer pending.");
        }

        campRequestRepository.delete(req);
    }

    // Dashboard සඳහා අවශ්‍ය Methods ටික මෙන්න:
    public long getTotalCompletedDrives(Organization organization) {
        return campRequestRepository.countCompletedDrivesByOrganization(organization);
    }

    public int getTotalDonorsEngaged(Organization organization) {
        Integer total = campRequestRepository.sumDonorsByOrganization(organization);
        return total != null ? total : 0;
    }

    public CampRequest getUpcomingCamp(Organization organization) {
        return campRequestRepository.findUpcomingCampByOrganization(organization).orElse(null);
    }
}