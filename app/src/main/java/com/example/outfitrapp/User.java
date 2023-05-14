package com.example.outfitrapp;

public class User {

    private String email;
//    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmai(String imageURL) {
        this.email = imageURL;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String caption) {
//        this.password = caption;
//    }

    public User(String email){
        this.email = email;
//        this.password = password;
    }
}
