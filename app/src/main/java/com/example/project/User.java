package com.example.project;

public class User {
    private String familyName = "";
    private String userName = "";
    private String phone = "";
    private String password = "";

    public User() {}

    public User(String familyName, String userName, String phone, String password) {
        this.familyName = familyName;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
