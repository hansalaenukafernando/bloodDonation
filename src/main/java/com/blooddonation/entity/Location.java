package com.blooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "center_name", nullable = false)
    private String centerName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Location() {}

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
}