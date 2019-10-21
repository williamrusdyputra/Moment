package edu.bluejack19_1.moment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import edu.bluejack19_1.moment.util.Transformation;

public class ImageDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dialog);

        String url = getIntent().getStringExtra("url");

        ImageView mDialog = findViewById(R.id.your_image);

        Glide.with(this)
                .load(url)
                .override(450, 450)
                .transform(new Transformation(90f))
                .into(mDialog);

        mDialog.setClickable(true);

        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
