package com.rcorp.app.futurewallet;

public class SendRequestPOJO {
    String name;
    String uid, phone;
    char firstLetter;

    public SendRequestPOJO(String name, String uid, String phone, char  firstLetter) {
        this.name = name;
        this.uid = uid;
        this.phone = phone;
        this.firstLetter = firstLetter;
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

    public char getFirstLetter() {
        return firstLetter;
    }
}
