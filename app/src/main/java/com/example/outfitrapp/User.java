package com.example.outfitrapp;

public class User {

    // Class with the authenticated users
    private String email;
    private static String userId;


    //Getters and Setters

    public String getEmail() {
        return email;
    }
    private static String userID;

    public static void setUserId(String userId) {
        User.userID = userId;
    }

    public static String getUserId() {
        return userId;
    }

    public void setEmai(String imageURL) {
        this.email = imageURL;
    }


    public User(String email){
        this.email = email;
    }
}