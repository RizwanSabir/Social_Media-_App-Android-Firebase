package com.example.dosribaar.Model;


public class Post {
    String postId;
    String postImage;
    String postedBy;
    String postDescription;
    long PostedAt;
    int commentCount;
    int postlike;

    public Post(String postId, String postImage, String postedBy, String postDescription, long postedAt) {
        this.postId = postId;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.PostedAt = postedAt;
    }

    public Post() {
    }

    public int getCommentCount() {
        return this.commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getPostlike() {
        return this.postlike;
    }

    public void setPostlike(int postlike) {
        this.postlike = postlike;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return this.postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public void setPostedAt(long postedAt) {
        PostedAt = postedAt;
    }
}
