package com.blooddonation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "priority", nullable = false)
    private String priority; // Standard / STAT

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "ward", nullable = false)
    private String ward;

    // Patient Details
    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(name = "patient_age", nullable = false)
    private Integer patientAge;

    @Column(name = "patient_gender", nullable = false)
    private String patientGender;

    @Column(name = "patient_id_number")
    private String patientIdNumber;

    @Column(name = "clinical_notes", length = 500)
    private String clinicalNotes;

    @Column(name = "status", nullable = false)
    private String status = "Pending";

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    // මෙහි ඇති අනෙකුත් fields එලෙසම පවතී, මේවා අලුතින් එකතු කරන්න:

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "cool_box_id")
    private CoolBox coolBox;



    public BloodRequest() {}

    // Getters and Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Integer getPatientAge() { return patientAge; }
    public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }

    public String getPatientGender() { return patientGender; }
    public void setPatientGender(String patientGender) { this.patientGender = patientGender; }

    public String getPatientIdNumber() { return patientIdNumber; }
    public void setPatientIdNumber(String patientIdNumber) { this.patientIdNumber = patientIdNumber; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    // Getters and Setters
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public CoolBox getCoolBox() { return coolBox; }
    public void setCoolBox(CoolBox coolBox) { this.coolBox = coolBox; }
}