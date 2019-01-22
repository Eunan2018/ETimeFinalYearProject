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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    // Create EditText references
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirmPassword;

    // Create Login Button
    private Button mButtonRegister;

    // Create FirebaseAuth reference
    private FirebaseAuth mFirebaseAuth;

    // Create ProgressDialog
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialise EditTexts
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mConfirmPassword = findViewById(R.id.register_confirm_password);

        // Initialise Register Button
        mButtonRegister = findViewById(R.id.register_button);

        // Initialise FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        // Trigger Register button when clicked
        mButtonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String confirmPassword = mConfirmPassword.getEditText().getText().toString();

                // Validate credentials
                if (!validateEmail(email)) {
                    mEmail.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    mPassword.setError("Not a valid password!");
                } else if (!confirmPassword.equals(password)) {
                    mConfirmPassword.setError("Passwords don't match!!");
                } else {
                    mEmail.setErrorEnabled(false);
                    mPassword.setErrorEnabled(false);
                    mConfirmPassword.setErrorEnabled(false);
                    mProgressDialog.setTitle("Register User");
                    mProgressDialog.setMessage("Please wait while we register your account !");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    registerUser(email, password);
                }
                Log.d(TAG, "onClick: ends");
            }
        });
    }

    private void registerUser(String email, String password) {
        Log.d(TAG, "registerUser: starts");
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, CreateProfile.class);
                                startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Successfully Registered, ", Toast.LENGTH_LONG).show();
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


    // Move to Login Activity if already Registered
    public void logIn(View view) {
        Log.d(TAG, "logIn: starts");
        Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }
}
