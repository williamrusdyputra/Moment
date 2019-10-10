package com.myapp.moment.model;

import java.util.ArrayList;

public class User {

    private String username;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private String description;
    private String profilePictureUrl;
    private ArrayList<Picture> pictures;

    public User(String username, int postCount, int followerCount, int followingCount, String description, ArrayList<Picture> pictures) {
        this.username = username;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.description = description;
        this.pictures = pictures;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
