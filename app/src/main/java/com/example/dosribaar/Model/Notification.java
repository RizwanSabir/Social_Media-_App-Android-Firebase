package com.example.dosribaar.Model;

public class Notification {
    String notfby;
    long notfAt;
    String type;
    String postBy;
    String postId;
    boolean checkOpen;
    String NotfId;

    public String getNotfId() {
        return NotfId;
    }

    public void setNotfId(String notfId) {
        NotfId = notfId;
    }

    public boolean isCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public String getNotfby() {
        return notfby;
    }

    public void setNotfby(String notfby) {
        this.notfby = notfby;
    }

    public void setNotfAt(long notfAt) {
        this.notfAt = notfAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


}
