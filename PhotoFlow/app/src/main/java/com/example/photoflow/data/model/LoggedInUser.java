package com.example.photoflow.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String email;

    public LoggedInUser( String displayName, String email) {

        this.displayName = displayName;
        this.email = email;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}