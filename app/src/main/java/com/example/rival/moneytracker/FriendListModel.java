package com.example.rival.moneytracker;

public class FriendListModel {
    String name;
    String uid;
    String phone;
  public FriendListModel(String name, String uid, String phone)
  {
      this.name=name;
      this.uid=uid;
      this.phone=phone;
  }
    public String getName() {
        return name;
    }

    public String getuid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

}
