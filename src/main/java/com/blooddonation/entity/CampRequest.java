package com.blooddonation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Camp_Requests")
public class CampRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(name = "camp_name", nullable = false)
    private String campName;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "expected_donors")
    private Integer expectedDonors;

    @Column(name = "setup_type")
    private String setupType;

    @Column(name = "location_details", columnDefinition = "TEXT")
    private String locationDetails;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    @Column(name = "status")
    private String status = "Pending";

    public CampRequest() {}

    // Getters and Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public String getCampName() { return campName; }
    public void setCampName(String campName) { this.campName = campName; }

    public LocalDate getPreferredDate() { return preferredDate; }
    public void setPreferredDate(LocalDate preferredDate) { this.preferredDate = preferredDate; }

    public Integer getExpectedDonors() { return expectedDonors; }
    public void setExpectedDonors(Integer expectedDonors) { this.expectedDonors = expectedDonors; }

    public String getSetupType() { return setupType; }
    public void setSetupType(String setupType) { this.setupType = setupType; }

    public String getLocationDetails() { return locationDetails; }
    public void setLocationDetails(String locationDetails) { this.locationDetails = locationDetails; }

    public String getSpecialNotes() { return specialNotes; }
    public void setSpecialNotes(String specialNotes) { this.specialNotes = specialNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}