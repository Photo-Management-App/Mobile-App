package com.example.photoflow.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private static String displayName;
    private static String email;

    public LoggedInUser( String displayName, String email) {

        this.displayName = displayName;
        this.email = email;
    }
    public static String getDisplayName() {
        return displayName;
    }

    public static String getEmail() {
        return email;
    }
}