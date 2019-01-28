package com.eunan.tracey.etimefinalyearproject;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    // Layout
    private android.support.v7.widget.Toolbar toolbar;
    private TextInputLayout txtStatus;
    private Button btnSave;

    // Database
    DatabaseReference statusDatabase;
    FirebaseUser currentUser;

    // Progress Dialog
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        statusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        // Layout
        toolbar = findViewById(R.id.statusAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusText = getIntent().getStringExtra("statusText");

        txtStatus = findViewById(R.id.textViewStatusInput);
        btnSave = findViewById(R.id.buttonSave);

        txtStatus.getEditText().setText(statusText);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Saving...");
                progressDialog.show();
                String status = txtStatus.getEditText().getText().toString();

                statusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(StatusActivity.this, "Needs fixed", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }
        });
    }
}
