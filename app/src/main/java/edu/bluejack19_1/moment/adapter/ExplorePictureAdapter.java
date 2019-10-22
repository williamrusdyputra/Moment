package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import edu.bluejack19_1.moment.HomeActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.fragment.LikedFragment;
import edu.bluejack19_1.moment.util.DataUtil;

public class ExplorePictureAdapter extends RecyclerView.Adapter<ExplorePictureAdapter.ExplorePictureViewHolder> {

    private Context context;
    private ArrayList<String> urls;

    public ExplorePictureAdapter(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    public void setData(ArrayList<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExplorePictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.random_picture_item, parent, false);
        return new ExplorePictureViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExplorePictureViewHolder holder, int position) {

        Glide.with(context)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)
                .load(urls.get(position))
                .into(holder.image);

        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fab.setBackgroundTintList(AppCompatResources.getColorStateList(context, R.color.colorWhite));
                holder.fab.setImageTintList(AppCompatResources.getColorStateList(context, R.color.colorAccent));

                Bitmap bitmap = ((BitmapDrawable)holder.image.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] bytes = stream.toByteArray();
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ UUID.randomUUID().toString());
                ref.putBytes(bytes)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();
                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();

                                        ref2.child("users").child(DataUtil.user.userID).child("liked_pictures").push().setValue(imageURL);
                                        DataUtil.user.likedPictures.add(imageURL);
                                        LikedFragment fragment = (LikedFragment) ((HomeActivity)context).getSupportFragmentManager().getFragments().get(2);
                                        fragment.updateData();
                                    }
                                });
                            }
                        });
            }
        });

        holder.image.requestLayout();
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class ExplorePictureViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        FloatingActionButton fab;

        ExplorePictureViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.random_image);
            fab = itemView.findViewById(R.id.like_fab);
        }
    }
}
