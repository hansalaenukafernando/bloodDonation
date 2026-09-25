package com.blooddonation.repository;

import com.blooddonation.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Donor entity.
 * Handles database operations for donors.
 */
@Repository
public interface DonorRepository extends JpaRepository<Donor, Integer> {

    // Check if a donor exists by NIC to avoid duplicates
    boolean existsByNic(String nic);

    // Check if a donor exists by email
    boolean existsByEmail(String email);

    // Find donor by email (useful for login later)
    Optional<Donor> findByEmail(String email);

    // Public homepage: count of donors with a given status (e.g. "Active")
    long countByStatus(String status);
}