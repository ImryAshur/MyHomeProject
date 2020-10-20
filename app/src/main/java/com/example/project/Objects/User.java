package com.example.project.Objects;

import com.example.project.Objects.MyEvent;

import java.util.ArrayList;

public class User {
    private String key = "";
    private String familyName = "";
    private String userName = "";
    private String phone = "";
    private String password = "";
    private ArrayList<MyEvent> userMyEvents = new ArrayList<>();

    public User() {}

    public User(String key,String familyName, String userName, String phone, String password) {
        this.key = key;
        this.familyName = familyName;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
