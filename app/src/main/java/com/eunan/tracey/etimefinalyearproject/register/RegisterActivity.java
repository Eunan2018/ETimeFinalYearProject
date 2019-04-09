package com.eunan.tracey.etimefinalyearproject.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    // Create EditText references
    private EditText email;
    private EditText password;
    private EditText userName;

    private TextView login;

    // Create Login Button
    private Button register;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialise EditTexts
        email = findViewById(R.id.edit_text_reg_email);
        password = findViewById(R.id.edit_text_reg_password);
        userName = findViewById(R.id.edit_text_reg_user_name);
        login = findViewById(R.id.text_view_reg_login);
        // Initialise Register Button
        register = findViewById(R.id.button_sign_up);

        // Initialise FirebaseAuth
        progressDialog = new ProgressDialog(this);

        // Trigger Register button when clicked
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = RegisterActivity.this.email.getText().toString().trim();
                String password = RegisterActivity.this.password.getText().toString().trim();
                String userName = RegisterActivity.this.userName.getText().toString().trim();

                // Validate credentials
                if (!validateEmail(email)) {
                    RegisterActivity.this.email.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    RegisterActivity.this.password.setError("Not a valid password!");
                } else {
                    progressDialog.setTitle("Register User");
                    progressDialog.setMessage("Please wait while we register your account !");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    registerUser(userName, email, password);
                }
                Log.d(TAG, "onClick: ends");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void registerUser(final String displayName, final String email, String password) {
        Log.d(TAG, "registerUser: starts");
        String userRef = "Users";
        String tokenRef = "Token";
        DBHandler dbHandler = new DBHandler(RegisterActivity.this,userRef,tokenRef);
        dbHandler.registerUser(displayName,email,password);
        progressDialog.cancel();
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
