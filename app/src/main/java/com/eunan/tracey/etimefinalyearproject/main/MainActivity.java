
package com.eunan.tracey.etimefinalyearproject.main;

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

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.register.RegisterActivity;
import com.eunan.tracey.etimefinalyearproject.user.UserProfileActivity;
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

 interface EmailValidator {
    boolean validateEmail(CharSequence target);
}

public class MainActivity extends AppCompatActivity implements EmailValidator{
    private final String TAG = "MainActivity";

    private TextView register;
    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference tokenDatabaseReference;

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
        tokenDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Token");


        register = findViewById(R.id.text_view_login_register);


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
                    // -1
                    final String currentUserId = firebaseAuth.getCurrentUser().getUid();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            // update the token of the device if the users logs in on another device
                            String token = instanceIdResult.getToken();
                            tokenDatabaseReference.child(currentUserId).child("tokenId").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //TODO
                                }
                            });
                            userDatabaseReference.child(currentUserId).child("token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                                    startActivity(intent);
                                    finish();
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


    // Check length of password
    protected boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts " + password);
        return password.length() > 5;
    }

//    // Check email format
//    protected boolean validateEmail(String email) {
//
//    }

    @Override
    public boolean validateEmail(CharSequence email) {
        Log.d(TAG, "validateEmail: starts " + email);
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
