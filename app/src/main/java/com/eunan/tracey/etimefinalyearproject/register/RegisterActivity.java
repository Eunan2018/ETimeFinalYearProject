package com.eunan.tracey.etimefinalyearproject.register;

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
import com.eunan.tracey.etimefinalyearproject.token.Token;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.user.UserModel;
import com.eunan.tracey.etimefinalyearproject.user.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    // Create EditText references
    private EditText email;
    private EditText password;
    private EditText userName;

    private TextView login;

    // Create Login Button
    private Button register;

    // Create FirebaseAuth reference
    private FirebaseAuth auth;
    String token;
    String uid;
    private DatabaseReference userRef;
    private DatabaseReference tokenRef;
    // Create ProgressDialog
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
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });
        createToken();
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


    public void registerUser(final String displayName, final String email, String password) {
        Log.d(TAG, "registerUser: starts");

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // use the model class to populate the database
                    UserModel userModel = new UserModel(displayName, "default", "default", "default", email, token);
                    userRef.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Successfully Registered, ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error, could not create user", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void createToken(){
        tokenRef = FirebaseDatabase.getInstance().getReference().child("Token").child(uid);
        Token tokenModel = new Token(token, uid);
        tokenRef.setValue(tokenModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
