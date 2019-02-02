package com.example.rival.moneytracker;

public class FriendPOJO {
    char firstLetter;
    String name, uid, phone;

    public FriendPOJO(char firstLetter, String name, String uid, String phone) {
        this.firstLetter = firstLetter;
        this.name = name;
        this.uid = uid;
        this.phone = phone;
    }

    public char getFirstLetter() {
        return firstLetter;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }
}
