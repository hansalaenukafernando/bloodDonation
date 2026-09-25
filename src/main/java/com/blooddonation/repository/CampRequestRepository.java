package com.blooddonation.repository;

import com.blooddonation.entity.CampRequest;
import com.blooddonation.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampRequestRepository extends JpaRepository<CampRequest, Integer> {
    List<CampRequest> findByOrganization(Organization organization);

    // "Request a Blood Camp" page: only requests still in progress for this organization
    // (Pending / Confirmed) — Completed/Cancelled ones belong in Camp History instead.
    List<CampRequest> findByOrganizationAndStatusInOrderByPreferredDateDesc(Organization organization, List<String> statuses);

    // Camp requests still awaiting admin review/approval — used on the admin dashboard's Pending Approvals card
    List<CampRequest> findByStatus(String status);

    // Full history for an organization: every camp request regardless of status
    // (Pending, Confirmed, Completed, Cancelled), newest first.
    List<CampRequest> findByOrganizationOrderByPreferredDateDesc(Organization organization);

    // Admin "Camp Requests" management page: still in-progress requests (Pending / Confirmed)
    List<CampRequest> findByStatusNotInOrderByPreferredDateDesc(List<String> statuses);

    // Admin "Camp Requests History" page: finalized requests (Completed / Cancelled)
    List<CampRequest> findByStatusInOrderByPreferredDateDesc(List<String> statuses);

    @Query("SELECT COUNT(c) FROM CampRequest c WHERE c.organization = :org AND c.status = 'Completed'")
    long countCompletedDrivesByOrganization(@Param("org") Organization org);

    @Query("SELECT SUM(c.expectedDonors) FROM CampRequest c WHERE c.organization = :org AND c.status = 'Completed'")
    Integer sumDonorsByOrganization(@Param("org") Organization org);

    @Query("SELECT c FROM CampRequest c WHERE c.organization = :org AND (c.status = 'Confirmed' OR c.status = 'Approved') ORDER BY c.preferredDate ASC LIMIT 1")
    Optional<CampRequest> findUpcomingCampByOrganization(@Param("org") Organization org);

    // Public homepage: island-wide upcoming confirmed camps (any organization), soonest first
    @Query("SELECT c FROM CampRequest c WHERE (c.status = 'Confirmed' OR c.status = 'Approved') AND c.preferredDate >= :today ORDER BY c.preferredDate ASC")
    List<CampRequest> findUpcomingConfirmedCamps(@Param("today") LocalDate today);
}