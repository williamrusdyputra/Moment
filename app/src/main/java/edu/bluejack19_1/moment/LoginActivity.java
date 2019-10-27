package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import edu.bluejack19_1.moment.util.DataUtil;
import edu.bluejack19_1.moment.util.TextUtil;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private SignInButton googleButton;
    private LoginButton facebookButton;
    private final static int RC_SIGN_IN = 2;
    private CallbackManager mCallbackManager;
    private SharedPreferences sharedPref;

    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        checkFromRegis();

        setupGoogleButton();
        setupFacebookButton();
        setupDefaultButton();
    }

    private void checkFromRegis() {

        Intent intent = getIntent();

        if(intent.hasExtra("email") && intent.hasExtra("password")) {
            emailText.setText(intent.getStringExtra("email"));
            passwordText.setText(intent.getStringExtra("password"));
        }

    }

    private void init() {
        FirebaseApp.initializeApp(this);
        googleButton = findViewById(R.id.google_login_button);
        facebookButton = findViewById(R.id.fb_login_button);
        Button facebookCustomButton = findViewById(R.id.fb_button);
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);
        emailText = findViewById(R.id.email_edit_text);
        passwordText = findViewById(R.id.password_edit_text);
        TextView signUpText = findViewById(R.id.sign_up_text);
        TextView signUpText2 = findViewById(R.id.register_question);
        sharedPref = this.getSharedPreferences(getString(R.string.user_pref_key), MODE_PRIVATE);

        signUpText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            }
        });

        facebookCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookButton.performClick();
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

    private void setupFacebookButton() {
        facebookButton.setPermissions("email");
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                try {
                                                    final String email = object.getString("email");
                                                    DataUtil.setSharedPreference(getApplicationContext(), sharedPref, email, email.split("@")[0]);

                                                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            ArrayList<String> usernames = new ArrayList<>();
                                                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                String name = ds.child("username").getValue(String.class);
                                                                usernames.add(name);
                                                            }

                                                            if(!usernames.contains(email.split("@")[0])) {
                                                                DataUtil.storeUser(Objects.requireNonNull(email), mDatabase);
                                                                mAuth.createUserWithEmailAndPassword(email, "socialmedia");
                                                            }
                                                            gotoHome(email.split("@")[0]);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                );
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "email");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                // on cancel
                            }

                            @Override
                            public void onError(FacebookException error) {
                                // on error
                            }
                        });
            }
        });
    }

    private void setupGoogleButton() {
        setGoogleLoginButtonText(googleButton, getResources().getString(R.string.log_in_with_google));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    protected void setGoogleLoginButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(0, 0, 20, 0);
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                final String email = account.getEmail();
                DataUtil.setSharedPreference(this, sharedPref, email, account.getDisplayName());

                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> usernames = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.child("username").getValue(String.class);
                            usernames.add(name);
                        }

                        assert email != null;
                        if(!usernames.contains(email.split("@")[0])) {
                            DataUtil.storeUser(Objects.requireNonNull(email), mDatabase);
                            mAuth.createUserWithEmailAndPassword(email, "socialmedia");
                        }
                        gotoHome(Objects.requireNonNull(Objects.requireNonNull(account).getEmail()).split("@")[0]);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DataUtil.user.username = Objects.requireNonNull(account.getEmail()).split("@")[0];
                DataUtil.user.profilePictureUrl = "default";
            }
        } catch (ApiException e) {
            Log.w("ERROR", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void setupDefaultButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString();
                final String password = passwordText.getText().toString();

                if(TextUtil.validate(email)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.email_error), Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DataUtil.setSharedPreference(getApplicationContext(), sharedPref, email, password);
                                        gotoHome(email.split("@")[0]);
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.login_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkRequiredFields() {
        if (!emailText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty()) {
            loginButton.setEnabled(true);
            loginButton.setAlpha(1);
        } else {
            loginButton.setEnabled(false);
            loginButton.setAlpha((float)0.5);
        }
    }

    private void gotoHome(String username) {
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        DataUtil.user.username = username;
        DataUtil.user.profilePictureUrl = "default";
        homeIntent.putExtra(HomeActivity.EXTRA_DATA, username);
        startActivity(homeIntent);
        finish();
    }

}
