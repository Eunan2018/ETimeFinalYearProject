package com.eunan.tracey.etimefinalyearproject.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;


public class WelcomeActivity extends AppCompatActivity {

    // Layout
    private Button login;
   // private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Layout
        login = findViewById(R.id.button_login);
       // signUp = findViewById(R.id.button_sign_up);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
//
//        signUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signUpIntent = new Intent(WelcomeActivity.this,RegisterActivity.class);
//                startActivity(signUpIntent);
//            }
//        });
    }
}
