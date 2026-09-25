package com.blooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(name = "license_number", unique = true, nullable = false, length = 100)
    private String licenseNumber;

    @Column(name = "status")
    private String status = "Active"; // Active, Deactivated

    public Driver() {}

    // Getters and Setters
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}