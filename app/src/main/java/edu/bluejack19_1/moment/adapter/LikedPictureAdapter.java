package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.util.Transformation;

public class LikedPictureAdapter extends RecyclerView.Adapter<LikedPictureAdapter.LikedPictureHolder> {

    private Context context;
    private ArrayList<String> urls = new ArrayList<>();

    public LikedPictureAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> urls) {
        this.urls.clear();
        this.urls.addAll(urls);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LikedPictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.liked_item, parent, false);
        return new LikedPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LikedPictureHolder holder, final int position) {
        final String url = urls.get(position);

        Glide.with(context)
                .load(url)
                .override(450, 450) //1
                .centerCrop()
                .into(holder.image);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                holder.likeButton.setBackgroundTintList(AppCompatResources.getColorStateList(context, R.color.colorWhite));
                holder.likeButton.setImageTintList(AppCompatResources.getColorStateList(context, R.color.colorAccent));
                urls.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, urls.size());
            }

        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class LikedPictureHolder extends RecyclerView.ViewHolder {

        ImageView image;
        FloatingActionButton likeButton;

        LikedPictureHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_list_liked_picture);
            likeButton = itemView.findViewById(R.id.like_liked_fab);
        }
    }
}
