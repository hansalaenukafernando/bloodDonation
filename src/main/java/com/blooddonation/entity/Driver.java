package com.blooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;

    @NotBlank(message = "Driver name is required")
    @Size(min = 3, max = 150, message = "Driver name must be between 3 and 150 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z .']*$",
             message = "Driver name can only contain letters, spaces, dots and apostrophes")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^0\\d{9}$",
             message = "Contact number must be 10 digits and start with 0 (e.g. 0712345678)")
    @Column(nullable = false, length = 50)
    private String phone;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Za-z]{1,2}\\d{6,8}$",
             message = "License number must be like B1234567 (1-2 letters followed by 6-8 digits)")
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
