package edu.bluejack19_1.moment.model;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class UserJSON {
    @PropertyName("user_id")
    public String userID;
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
    @PropertyName("picture_urls")
    public List<String> pictureUrls;

    public UserJSON(String userID, String username, int postCount, int followerCount, int followingCount, String description, String profilePictureUrl, List<String> pictureUrls) {
        this.userID = userID;
        this.username = username;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.description = description;
        this.profilePictureUrl = profilePictureUrl;
        this.pictureUrls = pictureUrls;
    }

    public UserJSON() {
        // required empty constructor
    }
}
