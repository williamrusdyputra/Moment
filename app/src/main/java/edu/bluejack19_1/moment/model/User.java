package edu.bluejack19_1.moment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
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
    @PropertyName("liked_pictures")
    public List<String> likedPictures;

    public User(String userID, String username, int postCount, int followerCount, int followingCount,
                String description, String profilePictureUrl, List<String> pictureUrls,
                List<String> followerIDs, List<String> followingIDs, List<String> followingIDKeys,
                List<String> followerIDKeys, List<String> likedPictures) {
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
        this.likedPictures = likedPictures;
    }

    public User() {
        followingIDs = new ArrayList<>();
        followerIDs = new ArrayList<>();
        followingIDKeys = new ArrayList<>();
        followerIDKeys = new ArrayList<>();
        likedPictures = new ArrayList<>();
    }

    private User(Parcel in) {
        userID = in.readString();
        username = in.readString();
        postCount = in.readInt();
        followerCount = in.readInt();
        followingCount = in.readInt();
        description = in.readString();
        profilePictureUrl = in.readString();
        pictureUrls = in.createStringArrayList();
        followingIDs = in.createStringArrayList();
        followerIDs = in.createStringArrayList();
        followingIDKeys = in.createStringArrayList();
        followerIDKeys = in.createStringArrayList();
        likedPictures = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(username);
        parcel.writeInt(postCount);
        parcel.writeInt(followerCount);
        parcel.writeInt(followingCount);
        parcel.writeString(description);
        parcel.writeString(profilePictureUrl);
        parcel.writeStringList(pictureUrls);
        parcel.writeStringList(followingIDs);
        parcel.writeStringList(followerIDs);
        parcel.writeStringList(followingIDKeys);
        parcel.writeStringList(followerIDKeys);
        parcel.writeStringList(likedPictures);
    }
}
