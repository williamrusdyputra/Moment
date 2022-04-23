package edu.bluejack19_1.moment.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack19_1.moment.EditProfileActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.PersonalPictureAdapter;
import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.viewmodel.PersonalPictureViewModel;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private PersonalPictureAdapter adapter;
    private PersonalPictureViewModel viewmodel;
    private ProgressBar progressBar;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        Sprite sprite = new ThreeBounce();
        sprite.setColor(Color.BLACK);
        progressBar.setIndeterminateDrawable(sprite);
        progressBar.setVisibility(View.VISIBLE);

        final RecyclerView myPictures = view.findViewById(R.id.personal_recycler_view);
        adapter = new PersonalPictureAdapter(getContext());
        adapter.notifyDataSetChanged();
        myPictures.setAdapter(adapter);
        myPictures.setVisibility(View.GONE);

        final CircleImageView profilePicture = view.findViewById(R.id.profile_picture);
        Glide.with(this)
                .load(R.drawable.default_picture)
                .into(profilePicture);

        myPictures.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                myPictures.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                myPictures.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        viewmodel = ViewModelProviders.of(this).get(PersonalPictureViewModel.class);
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
                startActivityForResult(intent, 24);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 24) {
            if(resultCode == RESULT_OK) {
                final TextView desc = Objects.requireNonNull(getView()).findViewById(R.id.profile_description);
                final String[] newDesc = {""};
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(DataUtil.user.userID).child("description");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        newDesc[0] = dataSnapshot.getValue(String.class);
                        desc.setText(newDesc[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private Observer<List<String>> getPicture = new Observer<List<String>>() {
        @Override
        public void onChanged(List<String> pictureList) {
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

    public void updateData() {
        viewmodel.getPictures().observe(this, getPicture);
        final CircleImageView profilePicture = view.findViewById(R.id.profile_picture);
        if(DataUtil.user.profilePictureUrl.equals("default")) {
            Glide.with(this)
                    .load(R.drawable.default_picture)
                    .into(profilePicture);
        } else {
            Glide.with(this)
                    .load(DataUtil.user.profilePictureUrl)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(profilePicture);
        }
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
}
