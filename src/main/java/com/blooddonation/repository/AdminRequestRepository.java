package com.blooddonation.repository;

import com.blooddonation.entity.DonorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRequestRepository extends JpaRepository<DonorRequest, Integer> {
    List<DonorRequest> findAllByOrderByPreferredDateDesc();

    // Active management page: still in-progress requests (Pending / Confirmed)
    List<DonorRequest> findByStatusNotInOrderByPreferredDateDesc(List<String> statuses);

    // Donation Requests History page: finalized requests (Completed / Cancelled)
    List<DonorRequest> findByStatusInOrderByPreferredDateDesc(List<String> statuses);
}