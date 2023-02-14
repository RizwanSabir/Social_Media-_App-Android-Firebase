package com.example.dosribaar.Model;

public class Follow {
    String FollowedBy;
    long FollowedAt;

    public Follow(String followedBy, long followedAt) {
        FollowedBy = followedBy;
        FollowedAt = followedAt;
    }

    public Follow() {
    }


    public String getFollowedBy() {
        return FollowedBy;
    }

    public void setFollowedBy(String followedBy) {
        FollowedBy = followedBy;
    }

    public long getFollowedAt() {
        return FollowedAt;
    }

    public void setFollowedAt(long followedAt) {
        FollowedAt = followedAt;
    }
}
