package com.blooddonation.repository;

import com.blooddonation.entity.DonorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRequestRepository extends JpaRepository<DonorRequest, Integer> {

    // Find all requests for a specific donor
    List<DonorRequest> findByDonor_DonorIdOrderByPreferredDateDesc(Integer donorId);

    // Find only requests with the given statuses (used to split "active" vs "finalized" requests)
    List<DonorRequest> findByDonor_DonorIdAndStatusInOrderByPreferredDateDesc(Integer donorId, List<String> statuses);

    // Find the latest request for eligibility and next date calculation
    Optional<DonorRequest> findTopByDonor_DonorIdOrderByPreferredDateDesc(Integer donorId);

    // Count total donations/requests made by the donor
    long countByDonor_DonorId(Integer donorId);
}