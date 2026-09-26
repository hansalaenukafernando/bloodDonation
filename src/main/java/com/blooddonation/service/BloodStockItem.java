package com.blooddonation.service;

/**
 * Read-only view of one blood group's live availability, used to render
 * the "National Blood Stock Status" badges on the public homepage.
 */
public class BloodStockItem {

    private final String bloodGroup;
    private final int availableUnits;
    private final String statusLabel; // Critical, Low, Adequate, Optimal

    public BloodStockItem(String bloodGroup, int availableUnits, String statusLabel) {
        this.bloodGroup = bloodGroup;
        this.availableUnits = availableUnits;
        this.statusLabel = statusLabel;
    }

    public String getBloodGroup() { return bloodGroup; }
    public int getAvailableUnits() { return availableUnits; }
    public String getStatusLabel() { return statusLabel; }

    // true for groups that need highlighting (red styling) on the homepage
    public boolean isNeedsAttention() {
        return "Critical".equals(statusLabel) || "Low".equals(statusLabel);
    }
}