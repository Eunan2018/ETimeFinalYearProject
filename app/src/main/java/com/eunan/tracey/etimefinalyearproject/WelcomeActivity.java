package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class WelcomeActivity extends AppCompatActivity {

    // Layout
    private Button btnLogin;
    private Button btnSigUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Layout
        btnLogin = findViewById(R.id.buttonLogin);
        btnSigUp = findViewById(R.id.buttonSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(loginIntent);
            }
        });

        btnSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(WelcomeActivity.this,RegisterActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}
