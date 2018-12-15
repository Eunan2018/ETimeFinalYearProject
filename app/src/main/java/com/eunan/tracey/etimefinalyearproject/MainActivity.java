package com.eunan.tracey.etimefinalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    // Create EditText references
    TextInputLayout mEmail;
    TextInputLayout mPassword;

    // Create Login Button
    Button mButtonLogin;

    // Create ProgressDialog Reference
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialise Login Button
        mButtonLogin = findViewById(R.id.login_button);
        // Initialise Email and Password EditTexts
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);

        // Trigger Login when button is clicked
        mButtonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Initialise ProgressDialog
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setTitle("Login");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);

                // Get credentials from EditTexts
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                // Validate credentials
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    mPassword.setError("Not a valid password!");
                } else {
                    mEmail.setErrorEnabled(false);
                    mPassword.setErrorEnabled(false);
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
                }
                mProgressDialog.dismiss();
            }
        });
        Log.d(TAG, "onCreate: ends");
    }

    // Check length of password
    public boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts" + password);
        return password.length() > 5;
    }

    // Move to Register Activity if not already registered
    public void signUp(View view) {
        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
