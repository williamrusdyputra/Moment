package edu.bluejack19_1.moment.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.bluejack19_1.moment.EditProfileActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.PersonalPictureAdapter;
import edu.bluejack19_1.moment.model.Picture;
import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.viewmodel.PersonalPictureViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private PersonalPictureAdapter adapter;
    private RecyclerView myPictures;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Picture> pictures = new ArrayList<>();

        if(DataUtil.userJSON.pictureUrls != null) {
            for(String url : DataUtil.userJSON.pictureUrls) {
                pictures.add(new Picture(url));
            }
        }

        myPictures = view.findViewById(R.id.personal_recycler_view);
        adapter = new PersonalPictureAdapter(getContext(), pictures);
        adapter.notifyDataSetChanged();
        myPictures.setAdapter(adapter);

        PersonalPictureViewModel viewmodel = ViewModelProviders.of(this).get(PersonalPictureViewModel.class);
        viewmodel.getPictures().observe(this, getPicture);

        myPictures.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getChildAdapterPosition(view); // item position
                int spanCount = 3;
                int spacing = 8;//spacing between views in grid

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        myPictures.setLayoutManager(gridLayoutManager);

        Button editButton = view.findViewById(R.id.edit_profile_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private Observer<List<Picture>> getPicture = new Observer<List<Picture>>() {
        @Override
        public void onChanged(List<Picture> pictureList) {
            if(pictureList != null) {
                adapter.setData(pictureList);
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    public void setup(String description, int postCount, int followingCount, int followerCount) {
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        ArrayList<Picture> pictures = new ArrayList<>();

        if(DataUtil.userJSON.pictureUrls != null) {
            for (String url : DataUtil.userJSON.pictureUrls) {
                pictures.add(new Picture(url));
            }
        }

        if(!hidden) {
            adapter = new PersonalPictureAdapter(getActivity(), pictures);
            adapter.notifyDataSetChanged();
            myPictures.setAdapter(adapter);
        }
    }
}
