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
import edu.bluejack19_1.moment.model.UserJSON;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static String username = "";
    public static UserJSON userJSON = new UserJSON();

    public static void setSharedPreference(Context context, SharedPreferences sharedPref, String email, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.user_pref), email);
        editor.putString(context.getString(R.string.user_pass_pref), password);
        editor.apply();
    }

    public static void storeUser(String email, DatabaseReference mDatabase) {
        List<String> temp = new ArrayList<>();
        String userID = mDatabase.push().getKey();
        userJSON = new UserJSON(userID, email.split("@")[0],
                0, 0, 0, "Hi, I am " + email.split("@")[0],
                "", temp, temp, temp, temp, temp);
        if (userID != null) {
            mDatabase.child("users").child(userID).setValue(userJSON);
        }
    }

}
