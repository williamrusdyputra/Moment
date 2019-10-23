package edu.bluejack19_1.moment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.bluejack19_1.moment.util.DataUtil;

public class HomePictureViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> pictureUrls;

    private void updateUrls() {
        final Map<String, String> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(!Objects.equals(ds.child("user_id").getValue(), DataUtil.user.userID)) {
                        String userID = Objects.requireNonNull(ds.child("user_id").getValue()).toString();
                        if(DataUtil.user.followingIDs.contains(userID)) {
                            for (DataSnapshot ds2 : ds.child("picture_urls").getChildren()) {
                                map.put(Objects.requireNonNull(ds2.child("url").getValue()).toString(),
                                        Objects.requireNonNull(ds.child("username").getValue()).toString());
                            }
                        }
                    }
                }
                setData(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData(Map<String, String> map) {
        this.pictureUrls.postValue(map);
    }

    public LiveData<Map<String, String>> getUrls() {
        if(pictureUrls == null) {
            pictureUrls = new MutableLiveData<>();
        }
        updateUrls();
        return pictureUrls;
    }
}
