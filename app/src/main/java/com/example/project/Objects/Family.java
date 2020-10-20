package com.example.project.Objects;

import java.util.ArrayList;
import java.util.HashMap;

public class Family {
    private String key = "";
    private String familyName = "";
    private String managerPhone = "";
    private ArrayList<User> familyMembers = new ArrayList<>();
    private HashMap<String, MyEvent> familyMyEvents = new HashMap<>();
    //private ArrayList<MyEvent> familyMyEvents = new ArrayList<>();


    public Family() {}

    public Family(String key,String familyName,String managerPhone) {
        this.key = key;
        this.familyName = familyName;
        this.managerPhone = managerPhone;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public ArrayList<User> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(ArrayList<User> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public HashMap<String,MyEvent> getFamilyMyEvents() {
        return familyMyEvents;
    }

//    public ArrayList<MyEvent> getFamilyMyEvents() {
//        return familyMyEvents;
//    }

//    public void setFamilyMyEvents(ArrayList<MyEvent> familyMyEvents) {
//        this.familyMyEvents = familyMyEvents;
//    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public boolean equals(Family obj) {
        return this.familyName.equals(obj.familyName);
    }

    @Override
    public String toString() {
        return "Family -" + familyName+
                "nickname='" + key + '\'' +
                ", familyMembers=" + familyMembers +
                '}';
    }
}
