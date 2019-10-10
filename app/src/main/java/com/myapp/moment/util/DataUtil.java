package com.myapp.moment.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.myapp.moment.R;
import com.myapp.moment.model.UserJSON;

public class DataUtil {

    public static String username = "";
    public static UserJSON userJSON = new UserJSON("", "", 0, 0, 0, "", "");

    public static void setSharedPreference(Context context, SharedPreferences sharedPref, String email, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.user_pref), email);
        editor.putString(context.getString(R.string.user_pass_pref), password);
        editor.apply();
    }

    public static void storeUser(String email, DatabaseReference mDatabase) {
        String userID = mDatabase.push().getKey();
        UserJSON user = new UserJSON("U0" + Math.random() * 1000, email.split("@")[0],
                0, 0, 0, "Hi, I am " + email.split("@")[0],
                "");
        if (userID != null) {
            mDatabase.child("users").child(userID).setValue(user);
        }
    }

}
