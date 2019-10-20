package edu.bluejack19_1.moment.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.model.User;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static String username = "";
    public static User user = new User();
    public static ArrayList<User> people = new ArrayList<>();

    public static void setSharedPreference(Context context, SharedPreferences sharedPref, String email, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.user_pref), email);
        editor.putString(context.getString(R.string.user_pass_pref), password);
        editor.apply();
    }

    public static ArrayList<User> getUsers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.username = ds.child("username").getValue(String.class);
                    user.description = ds.child("description").getValue(String.class);
                    user.profilePictureUrl = ds.child("profile_picture_url").getValue(String.class);
                    user.postCount = ds.child("post_count").getValue(Integer.class);
                    user.followerCount = ds.child("follower_count").getValue(Integer.class);
                    user.followingCount = ds.child("following_count").getValue(Integer.class);
                    user.userID = ds.child("user_id").getValue(String.class);

                    if(!user.userID.equals(DataUtil.user.userID))
                        people.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return people;
    }

    public static void storeUser(String email, DatabaseReference mDatabase) {
        List<String> temp = new ArrayList<>();
        String userID = mDatabase.push().getKey();
        user = new User(userID, email.split("@")[0],
                0, 0, 0, "Hi, I am " + email.split("@")[0],
                "", temp, temp, temp, temp, temp, temp);
        if (userID != null) {
            mDatabase.child("users").child(userID).setValue(user);
        }
    }

}
