package com.example.dosribaar.Chat;


public class messageList {
    String name;
    String mobile;
    String lastMessage;
    String ProfileImage;
    String chatKey;
    String PID;
    int unseenMessage;

    public messageList(String name, String mobile,
                       String lastMessage, String ProfileImageLink, int unseenMessage, String chatKey, String PID) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.unseenMessage = unseenMessage;
        this.ProfileImage = ProfileImageLink;
        this.chatKey = chatKey;
        this.PID = PID;

    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessage() {
        return unseenMessage;
    }
}
