package com.example.rival.moneytracker;

public class PendTranClass {
    String amount,reason,opponentUid, name;
    String dateInMillis;

    public PendTranClass(String amount, String reason, String opponentUid, String name, String dateInMillis) {
        this.amount = amount;
        this.reason = reason;
        this.opponentUid = opponentUid;
        this.name = name;
        this.dateInMillis = dateInMillis;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOpponentUid() {
        return opponentUid;
    }

    public void setOpponentUid(String opponentUid) {
        this.opponentUid = opponentUid;
    }

    public String getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(String dateInMillis) {
        this.dateInMillis = dateInMillis;
    }
}
