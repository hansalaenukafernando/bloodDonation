package com.blooddonation.repository;

import com.blooddonation.entity.BloodBag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodBagRepository extends JpaRepository<BloodBag, Integer> {

    List<BloodBag> findByStatus(String status);

    // All blood bags collected from a given donor (used to surface lab test results in donation history)
    List<BloodBag> findByDonor_DonorId(Integer donorId);

    // Bags NOT of a given status — used to exclude "Used" bags from the live inventory list
    List<BloodBag> findByStatusNot(String status);

    // Available blood bags ගණන ගණනය කිරීම
    @Query("SELECT COUNT(b) FROM BloodBag b WHERE b.bloodGroup = :group AND UPPER(b.status) = 'AVAILABLE'")
    Integer countAvailableByBloodGroup(@Param("group") String group);

    @Query("SELECT b FROM BloodBag b WHERE b.expiryDate < :today AND b.status = 'Available'")
    List<BloodBag> findExpiredAvailableBags(@Param("today") LocalDate today);

    // Available bags of a given group, oldest expiry first (FIFO) — used to deduct stock on dispatch
    List<BloodBag> findByBloodGroupAndStatusOrderByExpiryDateAsc(String bloodGroup, String status);

}