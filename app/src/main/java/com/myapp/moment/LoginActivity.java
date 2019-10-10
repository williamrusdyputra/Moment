package com.myapp.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.moment.util.DataUtil;
import com.myapp.moment.util.TextUtil;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private SignInButton googleButton;
    private LoginButton facebookButton;
    private final static int RC_SIGN_IN = 2;
    private CallbackManager mCallbackManager;
    private SharedPreferences sharedPref;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        setupGoogleButton();
        setupFacebookButton();
        setupDefaultButton();
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
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        sharedPref = this.getSharedPreferences(getString(R.string.user_pref_key), MODE_PRIVATE);
        if(sharedPref.contains(getString(R.string.user_pref))) {
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    private void setupFacebookButton() {
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                handleFacebookAccessToken(loginResult.getAccessToken());
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

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            DataUtil.setSharedPreference(getApplicationContext(), sharedPref, user.getEmail(), user.getDisplayName());
                            DataUtil.storeUser(Objects.requireNonNull(user.getEmail()), mDatabase);
                            gotoHome(user.getEmail().split("@")[0]);
                        }
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
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null)
                DataUtil.setSharedPreference(this, sharedPref, account.getEmail(), account.getDisplayName());
            gotoHome(Objects.requireNonNull(Objects.requireNonNull(account).getEmail()).split("@")[0]);
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
        homeIntent.putExtra(HomeActivity.EXTRA_DATA, username);
        startActivity(homeIntent);
        finish();
    }

}
