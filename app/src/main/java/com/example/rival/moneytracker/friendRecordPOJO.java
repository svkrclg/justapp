package com.example.rival.moneytracker;

public class friendRecordPOJO {
    String reason, time;
    String isAddedByMe;
    int amount;
    String direction;

    public friendRecordPOJO(String reason,
                            String time, String isAddedByMe, int amount, String direction) {
        this.reason = reason;
        this.time = time;
        this.isAddedByMe = isAddedByMe;
        this.amount = amount;
        this.direction = direction;
    }

    public String getReason() {
        return reason;
    }

    public String getTime() {
        return time;
    }

    public String getAddedByMe() {
        return isAddedByMe;
    }

    public int getAmount() {
        return amount;
    }

    public String getDirection() {
        return direction;
    }
}
