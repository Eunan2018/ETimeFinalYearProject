
package com.eunan.tracey.etimefinalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

    private TextView register;
    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private TwitterLoginButton twitterLoginButton;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        // Initialise dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Login");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Initialise Login Button
        buttonLogin = findViewById(R.id.button_login);
        // Initialise Email and Password EditTexts
        // Initialise GoogleSignInButton
        signInButton = findViewById(R.id.google_signin_button);
        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);
        // Initialise FireBaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        // Initialise Twitter Kit
        twitterLoginButton = findViewById(R.id.twitter_login_button);

        register = findViewById(R.id.text_view_login_register);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
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
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        // Trigger Login when button is clicked
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 101);
            }
        });
        // Trigger Login when button is clicked
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = MainActivity.this.email.getText().toString();
                String password = MainActivity.this.password.getText().toString();

                // Validate credentials
                if (!validateEmail(email)) {
                    MainActivity.this.email.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    MainActivity.this.password.setError("Not a valid password!");
                } else {
                    progressDialog.show();
                    loginUser(email, password);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        Log.d(TAG, "onCreate: ends");
    }


    private void loginUser(String email, String password) {
        Log.d(TAG, "loginUser: starts");
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    final String currentUserId = firebaseAuth.getCurrentUser().getUid();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String token = instanceIdResult.getToken();
                            userDatabaseReference.child(currentUserId).child("token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Login Successful, ", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                } else {
                    progressDialog.hide();
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
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "onActivityResult: ends");
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
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
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_LONG).show();
                            //FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                            //                          finish();
                        } else {
                            progressDialog.hide();
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

}
