package com.myapp.moment.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.myapp.moment.R;
import com.myapp.moment.model.Picture;

import java.util.ArrayList;

public class PersonalPictureAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Picture> pictures;

    public PersonalPictureAdapter(Context context, ArrayList<Picture> pictures) {
        this.mContext = context;
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView image = new ImageView(mContext);
        image.setImageDrawable(mContext.getDrawable(R.drawable.ic_explore));
        return image;
    }
}
