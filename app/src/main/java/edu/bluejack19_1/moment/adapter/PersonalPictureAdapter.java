package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import edu.bluejack19_1.moment.ImageDialog;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.util.Transformation;

public class PersonalPictureAdapter extends RecyclerView.Adapter<PersonalPictureAdapter.PersonalPictureHolder> {

    private Context context;
    private ArrayList<String> pictures = new ArrayList<>();

    public PersonalPictureAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> pictureList) {
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
    public void onBindViewHolder(@NonNull PersonalPictureHolder holder, final int position) {

        Glide.with(context)
                .load(pictures.get(position))
                .override(1000, 1000) //1
                .centerCrop()
                .transform(new Transformation(90f))
                .into(holder.picture);

        holder.picture.setClickable(true);

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageDialog.class);
                intent.putExtra("url", pictures.get(position));
                context.startActivity(intent);
            }
        });
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
