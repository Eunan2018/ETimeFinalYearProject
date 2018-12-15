package com.eunan.tracey.etimefinalyearproject;

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
                    Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d(TAG, "onCreate: ends");
    }

    // Check length of password
    public boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts" + password);
        return password.length() > 5;
    }
}
