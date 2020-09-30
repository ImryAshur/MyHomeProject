package com.example.project;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Family {
    private String nickname;
    private ArrayList<User> familyMembers = new ArrayList<>();

    public Family() {}

    public Family(String nickname) {
        this.nickname = nickname;
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
