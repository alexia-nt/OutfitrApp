package com.example.outfitrapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    String userID,email,password;

    public User(){
    }

    public User(String userID,String email,String password){
        this.userID=userID;
        this.email=email;
        this.password=password;
    }

    public void setUserID(String userID){this.userID=userID;}

    public void setEmail(String email){this.email=email;}

    public void setPassword(String password){this.password=password;}

    public String getUserID(){return userID;}

    public String getPassword(){return password;}

    public String getEmail(){return email;}

    @Override
    public String toString(){
        return "User{"+
                "userID='"+userID+'\''+
                ", email='"+email+'\''+
                ", password=" +password+'\''+
                '}';
    }

}


