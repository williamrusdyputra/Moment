package com.myapp.moment.model;

import com.google.firebase.database.PropertyName;

public class UserJSON {
    @PropertyName("username")
    public String username;
    @PropertyName("post_count")
    public int postCount;
    @PropertyName("follower_count")
    public int followerCount;
    @PropertyName("following_count")
    public int followingCount;
    @PropertyName("description")
    public String description;
    @PropertyName("profile_picture_url")
    public String profilePictureUrl;

    public UserJSON(String userID, String username, int postCount, int followerCount, int followingCount, String description, String profilePictureUrl) {
        this.username = username;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.description = description;
        this.profilePictureUrl = profilePictureUrl;
    }
}
