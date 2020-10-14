package com.example.project;

import java.util.ArrayList;
import java.util.HashMap;

public class Family {
    private String key = "";
    private String nickname = "";
    private String managerPhone = "";
    private ArrayList<User> familyMembers = new ArrayList<>();
    private HashMap<String,MyEvent> familyMyEvents = new HashMap<>();
    //private ArrayList<MyEvent> familyMyEvents = new ArrayList<>();


    public Family() {}

    public Family(String nickname,String managerPhone) {
        this.key = managerPhone + nickname;
        this.nickname = nickname;
        this.managerPhone = managerPhone;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        return this.nickname.equals(obj.nickname);
    }

    @Override
    public String toString() {
        return "Family -" +
                "nickname='" + nickname + '\'' +
                ", familyMembers=" + familyMembers +
                '}';
    }
}
