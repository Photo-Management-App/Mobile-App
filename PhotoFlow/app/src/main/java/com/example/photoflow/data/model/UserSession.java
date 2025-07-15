package com.example.photoflow.data.model;

public class UserSession {

    private static LoggedInUser currentUser;

    public static void setUser(LoggedInUser user) {
        currentUser = user;
    }

    public static LoggedInUser getUser() {
        return currentUser;
    }

}
