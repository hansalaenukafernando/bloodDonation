package com.blooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Cool_Boxes")
public class CoolBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id")
    private Integer boxId;

    @Column(name = "box_code", unique = true, nullable = false)
    private String boxCode; // e.g. CBX-1042

    @Column(name = "current_temp")
    private String currentTemp = "4.0°C";

    @Column(name = "status")
    private String status = "Active"; // Active, Maintenance, Deactivated

    public CoolBox() {}

    // Getters and Setters
    public Integer getBoxId() { return boxId; }
    public void setBoxId(Integer boxId) { this.boxId = boxId; }

    public String getBoxCode() { return boxCode; }
    public void setBoxCode(String boxCode) { this.boxCode = boxCode; }

    public String getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(String currentTemp) { this.currentTemp = currentTemp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}