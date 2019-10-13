package edu.bluejack19_1.moment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import edu.bluejack19_1.moment.util.DataUtil;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout textInputUsername;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textInputUsername = findViewById(R.id.text_input_username);
        final TextInputLayout textInputDescription = findViewById(R.id.text_input_description);
        submitButton = findViewById(R.id.edit_button);

        Objects.requireNonNull(textInputUsername.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkRequiredFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();

                ref.child("users").child(DataUtil.userJSON.userID).child("description").setValue(Objects.requireNonNull(textInputDescription.getEditText()).getText().toString());
                ref.child("users").child(DataUtil.userJSON.userID).child("username").setValue(textInputUsername.getEditText().getText().toString());

                finish();
            }
        });
    }

    private void checkRequiredFields() {
        if (!Objects.requireNonNull(textInputUsername.getEditText()).getText().toString().isEmpty()) {
            submitButton.setEnabled(true);
            submitButton.setAlpha(1);
        } else {
            submitButton.setEnabled(false);
            submitButton.setAlpha((float)0.5);
        }
    }
}
