package com.example.outfitrapp;


import com.google.firebase.database.Exclude;

public class DataClass {
    private String key;
    private String imageURL;
    private String caption;
    public DataClass(){

    }


    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }



    public DataClass(String imageURL,String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }
}