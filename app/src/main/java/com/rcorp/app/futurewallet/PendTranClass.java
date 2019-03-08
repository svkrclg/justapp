package com.rcorp.app.futurewallet;

public class PendTranClass {
    String reason,opponentUid, name;
    int amount;
    long dateInMillis;
    String direction;
    Boolean isAddedByMe;
    String date;

    public String getDate() {
        return date;
    }

    public String getDirection() {
        return direction;
    }

    public PendTranClass(Boolean isAddedByMe, int  amount, String reason, String opponentUid, String name, long dateInMillis, String direction, String date) {
        this.amount = amount;
        this.reason = reason;
        this.opponentUid = opponentUid;
        this.name = name;
        this.dateInMillis = dateInMillis;
        this.isAddedByMe=isAddedByMe;
        this.direction=direction;
        this.date=date;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public Boolean getAddedByMe() {
        return isAddedByMe;
    }

    public void setAddedByMe(Boolean addedByMe) {
        isAddedByMe = addedByMe;
    }

}
