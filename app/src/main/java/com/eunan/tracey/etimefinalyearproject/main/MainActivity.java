
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
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
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

public class MainActivity extends AppCompatActivity implements EmailValidator {
    private final String TAG = "MainActivity";

    private TextView register;
    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private ProgressDialog progressDialog;


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

        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);

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
        String userRef = "Users";
        String tokenRef = "Token";
        DBHandler dbHandler = new DBHandler(MainActivity.this,userRef,tokenRef);
        dbHandler.login(email,password);
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
