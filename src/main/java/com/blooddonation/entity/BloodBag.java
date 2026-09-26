package com.blooddonation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "blood_bags")
public class BloodBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bag_id")
    private Integer bagId;

    // donor_id වෙනුවට Donor entity එක සමඟ සම්බන්ධ කිරීම (Table එකේ column name එක 'donor_id' ලෙසම පවතී)
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "status", nullable = false)
    private String status = "Available"; // Available, Discarded, Used

    @Column(name = "hepatitis_result")
    private String hepatitisResult;

    @Column(name = "hiv_result")
    private String hivResult;

    @Column(name = "syphilis_result")
    private String syphilisResult;

    @Column(name = "tested_date")
    private LocalDate testedDate;

    public BloodBag() {}

    // Getters and Setters
    public Integer getBagId() { return bagId; }
    public void setBagId(Integer bagId) { this.bagId = bagId; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHepatitisResult() { return hepatitisResult; }
    public void setHepatitisResult(String hepatitisResult) { this.hepatitisResult = hepatitisResult; }

    public String getHivResult() { return hivResult; }
    public void setHivResult(String hivResult) { this.hivResult = hivResult; }

    public String getSyphilisResult() { return syphilisResult; }
    public void setSyphilisResult(String syphilisResult) { this.syphilisResult = syphilisResult; }

    public LocalDate getTestedDate() { return testedDate; }
    public void setTestedDate(LocalDate testedDate) { this.testedDate = testedDate; }
}