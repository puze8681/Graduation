package com.example.sunrin.myapplication.Data;

public class PhotoModel {

    private String booktoken, summary, photo;

    public PhotoModel(String booktoken, String summary, String photo) {
        this.booktoken = booktoken;
        this.summary = summary;
        this.photo = photo;
    }

    public String getBooktoken() {
        return booktoken;
    }

    public void setBooktoken(String booktoken) {
        this.booktoken = booktoken;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
