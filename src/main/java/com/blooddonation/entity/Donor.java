package com.blooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donor_id")
    private Integer donorId;

    @Column(unique = true, nullable = false, length = 20)
    private String nic;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "status")
    private String status = "Active"; // Active, Deactivated

    public Donor() {}

    // Getters and Setters
    public Integer getDonorId() { return donorId; }
    public void setDonorId(Integer donorId) { this.donorId = donorId; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}