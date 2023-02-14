package com.example.dosribaar.Chat;

public class ChatUser {


    boolean Check;
    long Time;
    String Message;
    String UID;

    public ChatUser(String message, String UID, boolean check, long Time) {
        Message = message;
        this.UID = UID;
        this.Check = check;
        this.Time = Time;

    }

    public ChatUser() {
    }


    public boolean isCheck() {
        return Check;
    }

    public void setCheck(boolean check) {
        Check = check;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
