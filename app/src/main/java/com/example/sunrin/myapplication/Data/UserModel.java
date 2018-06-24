package com.example.sunrin.myapplication.Data;

public class UserModel {

    private String username, schoolName, schoolNumber, birthday, password, usertoken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(String schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertoken() { return usertoken; }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public UserModel(String username, String schoolName, String schoolNumber, String birthday, String password, String usertoken) {
        this.username = username;
        this.schoolName = schoolName;
        this.schoolNumber = schoolNumber;
        this.birthday = birthday;
        this.password = password;
        this.usertoken = usertoken;
    }
}
