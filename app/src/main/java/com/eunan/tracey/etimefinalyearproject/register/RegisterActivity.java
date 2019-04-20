package com.eunan.tracey.etimefinalyearproject.register;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    // Create EditText references
    private EditText email;
    private EditText password;
    private EditText userName;
    private RadioGroup rdoGroup;
    private RadioButton rdoTitle;

    private TextView login;

    // Create Login Button
    private Button register;



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
        rdoGroup = findViewById(R.id.radio_group_reg);
        // Initialise Register Button
        register = findViewById(R.id.button_sign_up);

        // Trigger Register button when clicked
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                // Get credentials from EditTexts
                String email = RegisterActivity.this.email.getText().toString().trim();
                String password = RegisterActivity.this.password.getText().toString().trim();
                String userName = RegisterActivity.this.userName.getText().toString().trim();
                // get selected radio button from radioGroup
                int selectedId = rdoGroup.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: selectedID" + selectedId);

                if(String.valueOf(selectedId).equals("-1")){
                    Toast.makeText(RegisterActivity.this, "Need to select title", Toast.LENGTH_SHORT).show();
                }else{
                    // find the radiobutton by returned id
                    rdoTitle = findViewById(selectedId);
                    String title = rdoTitle.getText().toString();

                    // Validate credentials
                    if (!validateEmail(email)) {
                        RegisterActivity.this.email.setError("Not a valid email address!");
                    } else if (!validatePassword(password)) {
                        RegisterActivity.this.password.setError("Not a valid password!");
                    } else {
                        registerUser(userName, email, password,title);
                    }
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

    private void registerUser(final String displayName, final String email, String password,String title) {
        Log.d(TAG, "registerUser: starts");

        String userRef = "Users";
        String tokenRef = "Token";
        DBHandler dbHandler = new DBHandler(RegisterActivity.this,userRef,tokenRef);
        dbHandler.registerUser(displayName,email,password,title);
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
