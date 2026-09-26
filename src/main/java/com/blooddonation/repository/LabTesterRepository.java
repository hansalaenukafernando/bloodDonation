package com.blooddonation.repository;

import com.blooddonation.entity.LabTester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTesterRepository extends JpaRepository<LabTester, Integer> {
    Optional<LabTester> findByEmail(String email);
}