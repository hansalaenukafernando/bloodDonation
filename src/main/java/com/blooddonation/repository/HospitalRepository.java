package com.blooddonation.repository;

import com.blooddonation.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    Optional<Hospital> findByEmail(String email);
    Optional<Hospital> findByLicenseNumber(String licenseNumber);
}