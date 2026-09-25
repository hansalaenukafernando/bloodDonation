package com.blooddonation.repository;

import com.blooddonation.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    // Drivers that are not currently tied to an active (Delivering) delivery
    @Query("SELECT d FROM Driver d WHERE d.driverId NOT IN " +
           "(SELECT r.driver.driverId FROM BloodRequest r WHERE r.status = 'Delivering' AND r.driver IS NOT NULL)")
    List<Driver> findAvailableDrivers();
}