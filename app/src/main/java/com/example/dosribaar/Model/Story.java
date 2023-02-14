package com.example.dosribaar.Model;

import java.util.ArrayList;

public class Story {
    String storyby;
    String latestImage;
    ArrayList<UserStory> userStories = new ArrayList<>();
    long Time;

    public Story(String storyby, String latestImage, long time) {
        this.storyby = storyby;
        this.latestImage = latestImage;
        this.Time = time;
    }

    public Story() {
    }

    public long getTime() {
        return this.Time;
    }

    public void setTime(long time) {
        this.Time = time;
    }

    public ArrayList<UserStory> getUserStories() {
        return userStories;
    }

    public void setUserStories(ArrayList<UserStory> userStories) {
        this.userStories = userStories;
    }

    public String getStoryby() {
        return storyby;
    }

    public void setStoryby(String storyby) {
        this.storyby = storyby;
    }

    public String getLatestImage() {
        return latestImage;
    }

    public void setLatestImage(String latestImage) {
        this.latestImage = latestImage;
    }
}
