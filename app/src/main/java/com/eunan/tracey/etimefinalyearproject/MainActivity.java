package com.eunan.tracey.etimefinalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    // Create EditText references
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    // Create Login Button
    private Button mButtonLogin;
    // Create GoogleSignInButton
    private SignInButton mSignInButton;
    // Create ProgressDialog
    private ProgressDialog mProgressDialog;
    // Create FireBaseAuth reference
    private FirebaseAuth mFirebaseAuth;
    // Create GoogleSignIn reference
    private GoogleSignInClient mGoogleSignInClient;
    // Create Twitter Login Button
    private TwitterLoginButton mTwitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        // Initialise dialog
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setTitle("Login");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        // Initialise Login Button
        mButtonLogin = findViewById(R.id.login_button);
        // Initialise Email and Password EditTexts
        // Initialise GoogleSignInButton
        mSignInButton = findViewById(R.id.google_signin_button);
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        // Initialise FireBaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Initialise Twitter Kit
        mTwitterLoginButton = findViewById(R.id.twitter_login_button);
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                handleTwitterSession(result.data);
                Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Initialise GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        // Trigger Login when button is clicked
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.show();
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, 101);
            }
        });
        // Trigger Login when button is clicked
        mButtonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                // Validate credentials
                if (!validateEmail(email)) {
                    mEmail.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    mPassword.setError("Not a valid password!");
                } else {
                    mEmail.setErrorEnabled(false);
                    mPassword.setErrorEnabled(false);
                    mProgressDialog.show();
                    loginUser(email, password);
                }
            }
        });
        Log.d(TAG, "onCreate: ends");
    }


    private void loginUser(String email, String password) {
        Log.d(TAG, "loginUser: starts");
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Login Successful, ", Toast.LENGTH_LONG).show();
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(MainActivity.this, "Error, login failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: starts");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "onActivityResult: ends");
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Login Successful, ",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle: starts");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_LONG).show();
                            //FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                            startActivity(intent);
                            //                          finish();
                        } else {
                            mProgressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error Login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        Log.d(TAG, "firebaseAuthWithGoogle: ends");
    }

    // Check length of password
    private boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts " + password);
        return password.length() > 5;
    }

    // Check email format
    private boolean validateEmail(String email) {
        Log.d(TAG, "validateEmail: starts " + email);
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Move to Register Activity if not already registered
    public void signUp(View view) {
        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
