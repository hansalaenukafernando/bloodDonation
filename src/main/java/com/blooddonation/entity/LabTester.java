package com.blooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Lab_Testers")
public class LabTester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tester_id")
    private Integer testerId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "status")
    private String status = "Active"; // Active, Deactivated

    public LabTester() {}

    // Getters and Setters
    public Integer getTesterId() { return testerId; }
    public void setTesterId(Integer testerId) { this.testerId = testerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}