package edu.bluejack19_1.moment.model;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
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
    @PropertyName("followings")
    public List<String> followingIDs;
    @PropertyName("followers")
    public List<String> followerIDs;
    @PropertyName("following_keys")
    public List<String> followingIDKeys;
    @PropertyName("follower_keys")
    public List<String> followerIDKeys;

    public UserJSON(String userID, String username, int postCount, int followerCount, int followingCount,
                    String description, String profilePictureUrl, List<String> pictureUrls,
                    List<String> followerIDs, List<String> followingIDs, List<String> followingIDKeys,
                    List<String> followerIDKeys) {
        this.userID = userID;
        this.username = username;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.description = description;
        this.profilePictureUrl = profilePictureUrl;
        this.pictureUrls = pictureUrls;
        this.followerIDs = followerIDs;
        this.followingIDs = followingIDs;
        this.followerIDKeys = followerIDKeys;
        this.followingIDKeys = followingIDKeys;
    }

    public UserJSON() {
        followingIDs = new ArrayList<>();
        followerIDs = new ArrayList<>();
        followingIDKeys = new ArrayList<>();
        followerIDKeys = new ArrayList<>();
    }
}
