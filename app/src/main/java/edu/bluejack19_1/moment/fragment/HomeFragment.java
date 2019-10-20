package edu.bluejack19_1.moment.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.HomePictureAdapter;
import edu.bluejack19_1.moment.adapter.StoryAdapter;
import edu.bluejack19_1.moment.model.Story;
import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.viewmodel.HomePictureViewModel;

public class HomeFragment extends Fragment {

    private HomePictureAdapter adapter;
    private StoryAdapter storyAdapter;
    private ArrayList<Story> storyList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.home_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new HomePictureAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        HomePictureViewModel viewModel = ViewModelProviders.of(this).get(HomePictureViewModel.class);
        viewModel.getUrls().observe(this, observer);

        getStory();

        RecyclerView recyclerViewStory = view.findViewById(R.id.home_stories);
        recyclerViewStory.setHasFixedSize(true);
        recyclerViewStory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerViewStory.setAdapter(storyAdapter);
    }

    private Observer<Map<String, String>> observer = new Observer<Map<String, String>>() {
        @Override
        public void onChanged(Map<String, String> urls) {
            if(urls != null) {
                adapter.setData(urls);
            }
        }
    };

    private void getStory() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(DataUtil.user.userID != null) {
                    long currentTime = System.currentTimeMillis();
                    storyList.add(new Story("", 0, 0, "", DataUtil.user.userID));
                    for (String id : DataUtil.user.followingIDs) {
                        int count = 0;
                        Story story = null;
                        for (DataSnapshot ds : dataSnapshot.child(id).getChildren()) {
                            story = ds.getValue(Story.class);
                            if (story != null && currentTime > story.getTimeStart() && currentTime < story.getTimeEnd()) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            storyList.add(story);
                        }
                    }
                    Log.d("UNIQUE", storyList.size() + " ");
                    Log.d("UNIQUE", storyList.get(0).getUserID() + " ");
                    storyAdapter.setData(storyList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
