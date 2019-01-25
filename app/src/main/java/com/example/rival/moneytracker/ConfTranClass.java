package com.example.rival.moneytracker;

public class ConfTranClass {
    String opponentUid, name;
    int amount;
    String direction;
    public ConfTranClass(int  amount, String opponentUid, String name, String direction) {
        this.amount = amount;
        this.opponentUid = opponentUid;
        this.name = name;
        this.direction=direction;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }


    public String getDirection() {
        return direction;
    }

    public String getOpponentUid() {
        return opponentUid;
    }


}
