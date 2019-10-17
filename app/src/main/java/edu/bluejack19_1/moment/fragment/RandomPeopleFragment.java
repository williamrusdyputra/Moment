package edu.bluejack19_1.moment.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.PeopleListAdapter;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.util.DataUtil;

public class RandomPeopleFragment extends Fragment {

    private ArrayList<User> people;
    private PeopleListAdapter adapter;
    private ContentLoadingProgressBar progressBar;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public RandomPeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_random_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.people_progress_bar);
        progressBar.show();

        setupList(view);

        setupSearchView(view);
    }

    private void setupList(final View view) {
        people = new ArrayList<>();

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
                progressBar.hide();
                setupRecyclerView(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvList = view.findViewById(R.id.people_list);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeopleListAdapter(getContext(), people);
        rvList.setAdapter(adapter);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search_view);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

}
