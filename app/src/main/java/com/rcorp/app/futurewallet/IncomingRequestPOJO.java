package com.rcorp.app.futurewallet;

public class IncomingRequestPOJO {
    char firstLetter;
    String name, phone, uid;

    public IncomingRequestPOJO(char firstLetter, String name, String phone, String uid) {
        this.firstLetter = firstLetter;
        this.name = name;
        this.phone = phone;
        this.uid = uid;
    }

    public char getFirstLetter() {
        return firstLetter;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }
}
