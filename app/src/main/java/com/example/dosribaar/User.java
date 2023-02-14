package com.example.dosribaar;

public class User {
    String name;
    String id;
    String email;
    String password;
    String Bio;
    int followersCount;
    String UserId;
    String CoverPhoto;
    String ProfilePhoto;
    int followingCount;

    public User() {

    }

    public User(String name, String id, String email, String password, int followersCount, int followingCount) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.followersCount = followersCount;
        this.followingCount = followingCount;

    }

    public String getBio() {
        return this.Bio;
    }

    public void setBio(String bio) {
        this.Bio = bio;
    }

    public int getFollowersCount() {
        return this.followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getProfilePhoto() {
        return this.ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.ProfilePhoto = profilePhoto;
    }

    public int getFollowingCount() {
        return this.followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getCoverPhoto() {
        return this.CoverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.CoverPhoto = coverPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
