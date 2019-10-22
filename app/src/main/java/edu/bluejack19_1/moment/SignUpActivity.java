package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.util.TextUtil;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        setupDefaultButton();
    }

    private void init() {
        emailText = findViewById(R.id.email_sign_edit_text);
        passwordText = findViewById(R.id.password_sign_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);
        TextView loginQuestion = findViewById(R.id.login_question);
        mAuth = FirebaseAuth.getInstance();
        sharedPref = this.getSharedPreferences(getString(R.string.user_pref_key), MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loginQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // before text
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkRequiredFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // after text
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // before text
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkRequiredFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // after text
            }
        });

    }

    private void setupDefaultButton() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString();
                final String password = passwordText.getText().toString();

                if(TextUtil.validate(email)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.email_error), Toast.LENGTH_SHORT).show();
                } else if(password.length() < 7) {
                    Toast.makeText(getApplicationContext(), getString(R.string.password_error), Toast.LENGTH_SHORT).show();
                } else {
                    DataUtil.setSharedPreference(getApplicationContext(), sharedPref, email, password);

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> usernames = new ArrayList<>();
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                String name = ds.child("username").getValue(String.class);
                                usernames.add(name);
                            }
                            set(usernames, email, password);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void set(ArrayList<String> usernames, String email, String password) {
        if(usernames.contains(email.split("@")[0])) {
            Toast.makeText(getApplicationContext(), "Account already exists, please login", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password);
            DataUtil.storeUser(email, mDatabase);
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            DataUtil.username = email.split("@")[0];
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
    }

    private void checkRequiredFields() {
        if (!emailText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty()) {
            signUpButton.setEnabled(true);
            signUpButton.setAlpha(1);
        } else {
            signUpButton.setEnabled(false);
            signUpButton.setAlpha((float)0.5);
        }
    }

}
