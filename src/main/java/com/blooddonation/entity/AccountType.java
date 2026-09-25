package com.blooddonation.entity;

/**
 * The account types that can use "Forgot Password".
 * path  = URL prefix (/donor/..., /hospital/..., /organization/...)
 * label = text shown on the pages and in the email
 * icon  = Material Symbols icon name
 */
public enum AccountType {

    DONOR("donor", "Donor", "bloodtype"),
    HOSPITAL("hospital", "Hospital", "local_hospital"),
    ORGANIZATION("organization", "Organization", "groups");

    private final String path;
    private final String label;
    private final String icon;

    AccountType(String path, String label, String icon) {
        this.path = path;
        this.label = label;
        this.icon = icon;
    }

    public String getPath() { return path; }
    public String getLabel() { return label; }
    public String getIcon() { return icon; }

    /** Returns null when the path is not one of the supported account types. */
    public static AccountType fromPath(String path) {
        for (AccountType type : values()) {
            if (type.path.equalsIgnoreCase(path)) {
                return type;
            }
        }
        return null;
    }
}
