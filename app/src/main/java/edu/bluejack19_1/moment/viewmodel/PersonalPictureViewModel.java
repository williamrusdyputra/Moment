package edu.bluejack19_1.moment.viewmodel;

import android.util.Log;

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

public class PersonalPictureViewModel extends ViewModel {

    private MutableLiveData<List<String>> urls;

    private void updateUrls() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        if(DataUtil.user.userID != null) {
            DatabaseReference ref = database.child("users").child(DataUtil.user.userID).child("picture_urls");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> postUrls = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        postUrls.add(Objects.requireNonNull(ds.child("url").getValue()).toString());
                    }
                    setData(postUrls);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("UNIQUE", "onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void setData(ArrayList<String> urls) {
        this.urls.postValue(urls);
    }

    public LiveData<List<String>> getPictures() {
        if(urls == null) {
            urls = new MutableLiveData<>();
        }
        updateUrls();
        return urls;
    }

}
