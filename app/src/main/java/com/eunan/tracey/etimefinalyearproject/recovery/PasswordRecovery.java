package com.eunan.tracey.etimefinalyearproject.recovery;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.upload.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecovery extends AppCompatActivity {
    private Button btnReset;
    private EditText edtEmail;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        edtEmail = findViewById(R.id.edit_text_reset_pw);
        btnReset = findViewById(R.id.button_pw_reset);
        // Initialise Email and Password EditTexts
        auth = FirebaseAuth.getInstance();
        // Initialise dialog
        progressDialog = new ProgressDialog(PasswordRecovery.this);
        progressDialog.setMessage("Sending Password...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        toolbar = findViewById(R.id.time_sheet_app_bar);
        toolbar.setTitle("Password Recovery");
        setSupportActionBar(toolbar);

        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtEmail.getText().toString().isEmpty()) {
                    progressDialog.show();
                    auth.sendPasswordResetEmail(edtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.cancel();
                                        Toast.makeText(PasswordRecovery.this,
                                                "New Password sent to email address", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 1000);
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(PasswordRecovery.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    edtEmail.setFocusable(true);
                    Toast.makeText(PasswordRecovery.this, "Email must be completed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
