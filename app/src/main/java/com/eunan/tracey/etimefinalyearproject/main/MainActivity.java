
package com.eunan.tracey.etimefinalyearproject.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.register.RegisterActivity;
import com.twitter.sdk.android.core.Twitter;

interface EmailValidator {
    boolean validateEmail(CharSequence target);
}

public class MainActivity extends AppCompatActivity implements EmailValidator {
    private final String TAG = "MainActivity";

    private TextView register;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
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
        btnLogin = findViewById(R.id.button_login);
        // Initialise Email and Password EditTexts

        edtEmail = findViewById(R.id.edit_text_login_email);
        edtPassword = findViewById(R.id.edit_text_login_password);

        register = findViewById(R.id.text_view_login_register);
        // Trigger Login when button is clicked
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");
                // Get credentials from EditTexts
                String email = MainActivity.this.edtEmail.getText().toString();
                String password = MainActivity.this.edtPassword.getText().toString();
                // Validate credentials
                if (!validateEmail(email)) {
                    MainActivity.this.edtEmail.setError("Not a valid edtEmail address!");
                } else if (!validatePassword(password)) {
                    MainActivity.this.edtPassword.setError("Not a valid edtPassword!");
                } else {
                    progressDialog.show();
                    btnLogin.setEnabled(false);
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


    private void loginUser(final String email,final String password) {
        Log.d(TAG, "loginUser: starts");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
            }
        },1000);
        String userRef = "Users";
        String tokenRef = "Token";
        DBHandler dbHandler = new DBHandler(MainActivity.this,userRef,tokenRef);
        dbHandler.login(email,password);
        btnLogin.setEnabled(true);
        edtEmail.setText("");
        edtPassword.setText("");
    }



    // Check length of edtPassword
    protected boolean validatePassword(String password) {
        Log.d(TAG, "validatePassword: starts " + password);
        return password.length() > 5;
    }

//    // Check edtEmail format
//    protected boolean validateEmail(String edtEmail) {
//
//    }

    @Override
    public boolean validateEmail(CharSequence email) {
        Log.d(TAG, "validateEmail: starts " + email);
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }
}
