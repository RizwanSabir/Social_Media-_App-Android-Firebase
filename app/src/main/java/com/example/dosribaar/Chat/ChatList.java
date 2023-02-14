package com.example.dosribaar.Chat;


public class ChatList {
    String mobile;
    String name;
    String messgae;
    String date;
    String time;

    public ChatList(String mobile, String name, String messgae, String date, String time) {
        this.mobile = mobile;
        this.name = name;
        this.messgae = messgae;
        this.date = date;
        this.time = time;
    }


    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

