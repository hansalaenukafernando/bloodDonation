package com.blooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "Cool_Boxes")
public class CoolBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id")
    private Integer boxId;

    @NotBlank(message = "Box code is required")
    @Pattern(regexp = "^CBX-\\d{4}$",
             message = "Box code must be in CBX-0000 format (e.g. CBX-1042)")
    @Column(name = "box_code", unique = true, nullable = false)
    private String boxCode; // e.g. CBX-1042

    // Optional on the add form, so empty is allowed. If filled it must be a temperature.
    @Pattern(regexp = "^\\s*$|^-?\\d{1,2}(\\.\\d{1,2})?\\s*(°C|C|°c|c)?\\s*$",
             message = "Temperature must be a number like 4.0 or 4.0°C")
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
