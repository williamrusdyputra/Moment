package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import edu.bluejack19_1.moment.R;

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
    public void onBindViewHolder(@NonNull ExplorePictureViewHolder holder, int position) {

        Glide.with(context)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)
                .load(urls.get(position))
                .into(holder.image);

        holder.image.requestLayout();
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class ExplorePictureViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        ExplorePictureViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.random_image);
        }
    }
}
