package com.example.server.util;

public class Utilities {

    public static boolean isValidUserName(String username) {
        // Username must be at least 8 characters long
        if (username == null || username.length() < 8) {
            return false;
        }
        // Username must start with a char
        if (!Character.isLetter(username.charAt(0))) {
            return false;
        }
        // Valid username must contain:
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        // Loop over username characters and check each character
        for (char c : username.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) { // Username must be char or digit
                return false;
            }
        }
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    public static boolean isValidPassword(String password) {
        // Password must be at least 6 characters long
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasChar = true;
            } else {
                return false; // Invalid character (not digit or letter)
            }
        }

        return hasDigit && hasChar; // Must have both digit and character
    }

    private boolean isValidNameLastNameAge(String name, String lastName, String age) {
        try {
            // can be empty
            if (name.isEmpty() && lastName.isEmpty())
                return true;
            // name must contains only letter
            for (char c : name.toCharArray()) {
                if (!Character.isLetter(c))
                    return false;
            }
            // lastname must contain only letters
            for (char c : lastName.toCharArray()) {
                if (!Character.isLetter(c))
                    return false;
            }

            for (char c : age.toCharArray()) {
                if (!Character.isDigit(c))
                    return false;
            }

            return true;
        }
        catch (Exception e) {
            return false; // Return false if any exception occurs
        }
    }
}

