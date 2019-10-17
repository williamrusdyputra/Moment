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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.bluejack19_1.moment.util.DataUtil;

public class LikedViewModel extends ViewModel {

    private MutableLiveData<List<String>> urls;

    private void updateUrls() {
        final ArrayList<String> urls = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        if(DataUtil.user.userID != null) {
            ref.child("users").child(DataUtil.user.userID).child("liked_pictures").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        urls.add(Objects.requireNonNull(ds.getValue()).toString());
                    }
                    setData(urls);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setData(List<String> urls) {
        this.urls.postValue(urls);
    }

    public LiveData<List<String>> getUrls() {
        if(urls == null) {
            urls = new MutableLiveData<>();
        }
        updateUrls();
        return urls;
    }
}
