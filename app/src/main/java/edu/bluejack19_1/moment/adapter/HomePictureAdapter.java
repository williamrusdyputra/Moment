package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bluejack19_1.moment.HomeActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.fragment.LikedFragment;
import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.util.Transformation;

public class HomePictureAdapter extends RecyclerView.Adapter<HomePictureAdapter.PictureHolder> {

    private Context context;
    private Map<String, String> map = new HashMap<>();
    private ArrayList<String> pictureUrls = new ArrayList<>();

    public HomePictureAdapter(Context context) {
        this.context = context;
    }

    public void setData(Map<String, String> urls) {
        this.map.clear();
        this.map = urls;
        pictureUrls.clear();
        pictureUrls.addAll(map.keySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.picture_item, parent, false);
        return new PictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureHolder holder, int position) {
        final String url = pictureUrls.get(position);
        holder.username.setText(map.get(url));

        if(DataUtil.user.profilePictureUrl.equals("default")) {
            Glide.with(context)
                    .load(R.drawable.default_picture)
                    .into(holder.profilePicture);
        } else {
            Glide.with(context)
                    .load(DataUtil.user.profilePictureUrl)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(holder.profilePicture);
        }

        Glide.with(context)
                .load(url)
                .override(450, 450) //1
                .centerCrop()
                .transform(new Transformation(90f))
                .into(holder.image);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DataUtil.user.likedPictures.contains(url)) {
                    ref.child("users").child(DataUtil.user.userID).child("liked_pictures").push().setValue(url);
                    holder.likeButton.setBackgroundTintList(AppCompatResources.getColorStateList(context, R.color.colorWhite));
                    holder.likeButton.setImageTintList(AppCompatResources.getColorStateList(context, R.color.colorAccent));
                    DataUtil.user.likedPictures.add(url);
                    LikedFragment fragment = (LikedFragment) ((HomeActivity)context).getSupportFragmentManager().getFragments().get(2);
                    fragment.updateData();
                } else {
                    final Query query = ref.child("users").child(DataUtil.user.userID).child("liked_pictures").orderByValue().equalTo(url);
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().setValue(null);
                            query.removeEventListener(this);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9497a1")));
                    holder.likeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    DataUtil.user.likedPictures.remove(url);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return pictureUrls.size();
    }

    class PictureHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView image, profilePicture;
        FloatingActionButton likeButton;

        PictureHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.people_list_name);
            image = itemView.findViewById(R.id.item_list_picture);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            likeButton = itemView.findViewById(R.id.like_fab);
        }
    }
}
