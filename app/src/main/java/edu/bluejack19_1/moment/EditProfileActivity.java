package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack19_1.moment.util.DataUtil;

public class EditProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 32;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        profileImage = findViewById(R.id.profile_edit_picture);

        if(DataUtil.user.profilePictureUrl.equals("default")) {
            Glide.with(this)
                    .load(R.drawable.default_picture)
                    .into(profileImage);
        } else {
            Glide.with(this)
                    .load(DataUtil.user.profilePictureUrl)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(profileImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        final TextInputLayout textInputDescription = findViewById(R.id.text_input_description);
        Button submitButton = findViewById(R.id.edit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();

                ref.child("users").child(DataUtil.user.userID).child("description").setValue(Objects.requireNonNull(textInputDescription.getEditText()).getText().toString());

                DataUtil.user.description = textInputDescription.getEditText().getText().toString();
                setResult(RESULT_OK);

                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY_REQUEST_CODE) {
                assert data != null;
                final Uri image = data.getData();
                Glide.with(this)
                        .load(image)
                        .apply(new RequestOptions().placeholder(R.drawable.default_picture))
                        .into(profileImage);

                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile/" + UUID.randomUUID().toString());
                if (image != null) {
                    ref.putFile(image)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageURL = uri.toString();
                                            DataUtil.user.profilePictureUrl = imageURL;
                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                                            ref2.child("users").child(DataUtil.user.userID).child("profile_picture_url").setValue(imageURL);
                                            Toast.makeText(getApplicationContext(), "Changes will appear after some time", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                }

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
