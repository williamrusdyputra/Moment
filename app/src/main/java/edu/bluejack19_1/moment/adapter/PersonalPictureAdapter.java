package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.model.Picture;
import edu.bluejack19_1.moment.util.Transformation;

public class PersonalPictureAdapter extends RecyclerView.Adapter<PersonalPictureAdapter.PersonalPictureHolder> {

    private Context context;
    private ArrayList<Picture> pictures;

    public PersonalPictureAdapter(Context context, ArrayList<Picture> pictures) {
        this.context = context;
        this.pictures =pictures;
    }

    public void setData(List<Picture> pictureList) {
        this.pictures.clear();
        this.pictures.addAll(pictureList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonalPictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.personal_picture_item, parent, false);
        return new PersonalPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalPictureHolder holder, int position) {
        Picture picture = pictures.get(position);

        Glide.with(context)
                .load(picture.getUrl())
                .override(450, 450) //1
                .centerCrop()
                .transform(new Transformation(90f))
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class PersonalPictureHolder extends RecyclerView.ViewHolder {

        ImageView picture;

        PersonalPictureHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.picture);
        }
    }
}
