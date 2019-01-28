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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    // Create EditText references
    private EditText mEmail;
    private EditText mPassword;
    private EditText mUserName;

    private TextView mLogin;

    // Create Login Button
    private Button mButtonRegister;

    // Create FirebaseAuth reference
    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference databaseReference;

    // Create ProgressDialog
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialise EditTexts
        mEmail = findViewById(R.id.editTextRegUserName);
        mPassword = findViewById(R.id.editTextRegPasword);
        mUserName = findViewById(R.id.editTextRegUserName);
        mLogin = findViewById(R.id.textViewLogin);
        // Initialise Register Button
        mButtonRegister = findViewById(R.id.buttonSignUp);

        // Initialise FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        // Trigger Register button when clicked
        mButtonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String userName = mUserName.getText().toString().trim();

                // Validate credentials
                if (!validateEmail(email)) {
                    mEmail.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    mPassword.setError("Not a valid password!");
                } else {
                    mProgressDialog.setTitle("Register User");
                    mProgressDialog.setMessage("Please wait while we register your account !");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    registerUser(userName, email, password);
                }
                Log.d(TAG, "onClick: ends");
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void registerUser(final String displayName, String email, String password) {
        Log.d(TAG, "registerUser: starts");
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", displayName);
                    userMap.put("status", "Hey there!!!");
                    userMap.put("image", "default");
                    userMap.put("thumbImage", "default");

                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Successfully Registered, ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Error, could not create user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Check length of password
    public boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts " + password);
        return password.length() > 5;
    }

    // Check email format
    public boolean validateEmail(String email) {
        Log.d(TAG, "validateEmail: starts " + email);
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
