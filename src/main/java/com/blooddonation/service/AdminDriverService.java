package com.blooddonation.service;

import com.blooddonation.entity.Driver;
import com.blooddonation.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public void addDriver(String name, String phone, String licenseNumber) throws Exception {
        if (driverRepository.findByLicenseNumber(licenseNumber).isPresent()) {
            throw new Exception("A driver with this license number already exists!");
        }

        Driver driver = new Driver();
        driver.setName(name);
        driver.setPhone(phone);
        driver.setLicenseNumber(licenseNumber);
        driver.setStatus("Active");

        driverRepository.save(driver);
    }

    public void updateDriver(Integer driverId, String name, String phone, String licenseNumber) throws Exception {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new Exception("Driver not found"));

        driver.setName(name);
        driver.setPhone(phone);
        driver.setLicenseNumber(licenseNumber);

        driverRepository.save(driver);
    }

    public void updateDriverStatus(Integer driverId, String status) throws Exception {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new Exception("Driver not found"));

        driver.setStatus(status);
        driverRepository.save(driver);
    }

    public void deleteDriver(Integer driverId) {
        driverRepository.deleteById(driverId);
    }
}