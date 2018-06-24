package com.example.sunrin.myapplication.Data;

public class PhotobookModel {

    private String name, photo, summary, since, booktoken, usertoken;
    private int count;

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getBooktoken() {
        return booktoken;
    }

    public void setBooktoken(String booktoken) {
        this.booktoken = booktoken;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PhotobookModel(String name, String photo, String summary, String since, String booktoken, String usertoken, int count) {
        this.name = name;
        this.photo = photo;
        this.summary = summary;
        this.since = since;
        this.booktoken = booktoken;
        this.usertoken = usertoken;
        this.count = count;
    }
}
