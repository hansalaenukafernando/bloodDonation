package com.blooddonation.util;

import java.time.LocalDate;

/**
 * Site එක පුරාම පාවිච්චි කරන validation helper එක.
 *
 * හැම method එකක්ම:
 *   - හරි නම්  -> null
 *   - වැරදි නම් -> user ට පෙන්නන error message එක
 *
 * Controller එකක පාවිච්චි කරන විදිහ:
 *
 *   String error = Validate.first(
 *       Validate.name("Driver name", name),
 *       Validate.phone("Contact number", phone)
 *   );
 *   if (error != null) {
 *       ra.addFlashAttribute("errorMessage", error);
 *       return "redirect:/admin/drivers";
 *   }
 */
public final class Validate {

    private Validate() {}

    /** දුන්න messages අතරින් null නොවන පළවෙනි එක ගන්නවා. ඔක්කොම හරි නම් null. */
    public static String first(String... messages) {
        if (messages == null) return null;
        for (String m : messages) {
            if (m != null) return m;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // පොදු (basic) checks
    // ---------------------------------------------------------------

    /** හිස් වෙන්න බෑ (String). */
    public static String required(String label, String value) {
        if (isBlank(value)) return label + " is required";
        return null;
    }

    /** හිස් වෙන්න බෑ (Integer, dropdown id වගේ ඒවාට). */
    public static String required(String label, Object value) {
        if (value == null) return label + " is required";
        if (value instanceof String) return required(label, (String) value);
        return null;
    }

    /** අකුරු ගාන min සහ max අතර තියෙනවද කියලා බලනවා. */
    public static String text(String label, String value, int min, int max) {
        String req = required(label, value);
        if (req != null) return req;

        String v = value.trim();
        if (v.length() < min) return label + " must be at least " + min + " characters";
        if (v.length() > max) return label + " must not be longer than " + max + " characters";
        return null;
    }

    /** හිස් කරන්න පුළුවන්, දැම්මොත් max එක ඉක්මවන්න බෑ. */
    public static String optionalText(String label, String value, int max) {
        if (isBlank(value)) return null;
        if (value.trim().length() > max) return label + " must not be longer than " + max + " characters";
        return null;
    }

    /** dropdown/radio එකක තියෙන අගයක්ද කියලා බලනවා. */
    public static String oneOf(String label, String value, String... allowed) {
        String req = required(label, value);
        if (req != null) return req;

        for (String a : allowed) {
            if (a.equalsIgnoreCase(value.trim())) return null;
        }
        return "Please select a valid " + label.toLowerCase();
    }

    // ---------------------------------------------------------------
    // Personal details
    // ---------------------------------------------------------------

    /** නම් වලට: අකුරු, space, තිත, apostrophe විතරයි. */
    public static String name(String label, String value) {
        String len = text(label, value, 3, 150);
        if (len != null) return len;

        if (!value.trim().matches("^[A-Za-z][A-Za-z .'-]*$")) {
            return label + " can only contain letters, spaces, dots and hyphens";
        }
        return null;
    }

    /** ආයතන/රෝහල් නම් වලට: අකුරු + ඉලක්කම් ටිකක් allow කරනවා. */
    public static String title(String label, String value) {
        String len = text(label, value, 3, 150);
        if (len != null) return len;

        if (!value.trim().matches("^[A-Za-z0-9][A-Za-z0-9 .,'&()/-]*$")) {
            return label + " contains invalid characters";
        }
        return null;
    }

    /** ශ්‍රී ලංකා phone number: 0 න් පටන්ගෙන ඉලක්කම් 10ක්. */
    public static String phone(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        String v = value.replaceAll("[\\s-]", "");
        if (!v.matches("^0\\d{9}$")) {
            return label + " must be 10 digits starting with 0 (e.g. 0712345678)";
        }
        return null;
    }

    /** Email format එක. */
    public static String email(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        if (!value.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Please enter a valid " + label.toLowerCase();
        }
        if (value.trim().length() > 150) return label + " is too long";
        return null;
    }

    /** NIC: පරණ එක (ඉලක්කම් 9 + V/X) හරි අලුත් එක (ඉලක්කම් 12) හරි. */
    public static String nic(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        String v = value.trim().toUpperCase();
        if (!v.matches("^\\d{9}[VX]$") && !v.matches("^\\d{12}$")) {
            return label + " must be 9 digits with V/X (e.g. 991234567V) or 12 digits";
        }
        return null;
    }

    /** Blood group එක හරිද කියලා. */
    public static String bloodGroup(String label, String value) {
        return oneOf(label, value, "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    }

    // ---------------------------------------------------------------
    // Passwords
    // ---------------------------------------------------------------

    /** අඩුම තරමේ අකුරු 6ක්, අකුරකුයි ඉලක්කමකුයි තියෙන්න ඕන. */
    public static String password(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        if (value.length() < 6) return label + " must be at least 6 characters";
        if (value.length() > 100) return label + " is too long";
        if (!value.matches(".*[A-Za-z].*") || !value.matches(".*\\d.*")) {
            return label + " must contain at least one letter and one number";
        }
        return null;
    }

    /** Password දෙක සමානද කියලා. */
    public static String match(String label, String a, String b) {
        if (a == null || !a.equals(b)) return label + " do not match";
        return null;
    }

    // ---------------------------------------------------------------
    // ඉලක්කම් සහ දින
    // ---------------------------------------------------------------

    /** අගය min සහ max අතර තියෙන්න ඕන. */
    public static String number(String label, Integer value, int min, int max) {
        if (value == null) return label + " is required";
        if (value < min || value > max) return label + " must be between " + min + " and " + max;
        return null;
    }

    /** අද හරි ඊට පස්සේ හරි දවසක් වෙන්න ඕන. */
    public static String futureDate(String label, LocalDate value) {
        if (value == null) return label + " is required";
        if (value.isBefore(LocalDate.now())) return label + " cannot be in the past";
        return null;
    }

    /** අද හරි ඊට කලින් හරි දවසක් වෙන්න ඕන. */
    public static String notFutureDate(String label, LocalDate value) {
        if (value == null) return label + " is required";
        if (value.isAfter(LocalDate.now())) return label + " cannot be in the future";
        return null;
    }

    /** පළවෙනි දිනට පස්සේ දෙවෙනි දිනය තියෙන්න ඕන. */
    public static String afterDate(String laterLabel, LocalDate later, String earlierLabel, LocalDate earlier) {
        if (later == null || earlier == null) return null; // උඩින් වෙනම check වෙනවා
        if (!later.isAfter(earlier)) return laterLabel + " must be after " + earlierLabel.toLowerCase();
        return null;
    }

    /** "yyyy-MM-dd" text එකක් LocalDate එකක් බවට හරවනවා. වැරදි නම් null. */
    public static LocalDate parseDate(String value) {
        try {
            if (isBlank(value)) return null;
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    // ---------------------------------------------------------------
    // Project එකට විශේෂ ඒවා
    // ---------------------------------------------------------------

    /** Driver license number: අකුරු 1-2ක් + ඉලක්කම් 6-8ක් (උදා: B1234567). */
    public static String licenseNumber(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        if (!value.trim().matches("^[A-Za-z]{1,2}\\d{6,8}$")) {
            return label + " must be like B1234567 (1-2 letters followed by 6-8 digits)";
        }
        return null;
    }

    /** රෝහල් license: අකුරු/ඉලක්කම්/hyphen, අකුරු 4-50. */
    public static String hospitalLicense(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        if (!value.trim().matches("^[A-Za-z0-9-]{4,50}$")) {
            return label + " must be 4-50 characters (letters, numbers and hyphens only)";
        }
        return null;
    }

    /** Cool box code: CBX-0000 format එක. */
    public static String boxCode(String label, String value) {
        String req = required(label, value);
        if (req != null) return req;

        if (!value.trim().toUpperCase().matches("^CBX-\\d{4}$")) {
            return label + " must be in CBX-0000 format (e.g. CBX-1042)";
        }
        return null;
    }

    /** උෂ්ණත්වය: හිස් කරන්න පුළුවන්, දැම්මොත් 4.0 හරි 4.0°C හරි වගේ. */
    public static String temperature(String label, String value) {
        if (isBlank(value)) return null;

        if (!value.trim().matches("^-?\\d{1,2}(\\.\\d{1,2})?\\s*(°C|C|°c|c)?$")) {
            return label + " must be a number like 4.0 or 4.0°C";
        }
        return null;
    }

    // ---------------------------------------------------------------

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
