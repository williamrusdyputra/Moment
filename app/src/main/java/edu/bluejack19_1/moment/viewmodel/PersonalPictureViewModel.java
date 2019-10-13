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

import edu.bluejack19_1.moment.model.Picture;

public class PersonalPictureViewModel extends ViewModel {

    private MutableLiveData<List<Picture>> pictureUrls;

    private void updateUrls() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    DataSnapshot posts = ds.child("picture_urls");
                    Iterable<DataSnapshot> items = posts.getChildren();
                    ArrayList<String> postUrls = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        postUrls.add(item.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("UNIQUE", "onCancelled", databaseError.toException());
            }
        });
    }

    public LiveData<List<Picture>> getPictures() {
        if(pictureUrls == null) {
            pictureUrls = new MutableLiveData<>();
            updateUrls();
        }
        return pictureUrls;
    }

}
