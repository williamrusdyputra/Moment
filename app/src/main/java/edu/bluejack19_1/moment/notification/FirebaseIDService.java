package edu.bluejack19_1.moment.notification;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import edu.bluejack19_1.moment.util.DataUtil;

public class FirebaseIDService extends FirebaseMessagingService {

    public static String token;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        token = s;
        updateToken(s);
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        ref.child(DataUtil.user.userID).setValue(token1);
    }
}
