package com.myapp.moment.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.moment.R;
import com.myapp.moment.adapter.PersonalPictureAdapter;
import com.myapp.moment.model.Picture;
import com.myapp.moment.util.DataUtil;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private  DatabaseReference ref;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("users");

        final String username = DataUtil.username;

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("username").getValue(String.class);
                    String description = ds.child("description").getValue(String.class);
                    String url = ds.child("profile_picture_url").getValue(String.class);
                    int postCount = ds.child("post_count").getValue(Integer.class);
                    int followerCount = ds.child("follower_count").getValue(Integer.class);
                    int followingCount = ds.child("following_count").getValue(Integer.class);

                    DataUtil.userJSON.username = name;
                    DataUtil.userJSON.postCount = postCount;
                    DataUtil.userJSON.followerCount = followerCount;
                    DataUtil.userJSON.followingCount = followingCount;
                    DataUtil.userJSON.profilePictureUrl = url;
                    DataUtil.userJSON.description = description;

                    if(username.equals(name)) {
                        ref.removeEventListener(this);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("UNIQUE", "onCancelled", databaseError.toException());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(new Picture("1"));
        pictures.add(new Picture("2"));
        pictures.add(new Picture("3"));
        pictures.add(new Picture("4"));
        pictures.add(new Picture("5"));
        pictures.add(new Picture("6"));
        pictures.add(new Picture("7"));

        GridView pictureGrid = view.findViewById(R.id.personal_pictures);
        PersonalPictureAdapter pictureAdapter = new PersonalPictureAdapter(getContext(), pictures);
        pictureGrid.setAdapter(pictureAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    public void setup(String description, int postCount, int followingCount, int followerCount, String username) {
        TextView desc = Objects.requireNonNull(getView()).findViewById(R.id.profile_description);
        TextView pc = getView().findViewById(R.id.post_number);
        TextView fc = getView().findViewById(R.id.following_number);
        TextView fc2 = getView().findViewById(R.id.follower_number);
        desc.setText(description);

        String post = Integer.toString(postCount);
        String following = Integer.toString(followingCount);
        String follower = Integer.toString(followerCount);

        pc.setText(post);
        fc.setText(following);
        fc2.setText(follower);
    }

}
