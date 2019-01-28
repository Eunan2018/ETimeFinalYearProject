package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtEmployer;
    private TextView txtEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtEmployer = findViewById(R.id.textViewEmployer);
        txtEmployee = findViewById(R.id.textViewEmployee);


        txtEmployer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employerIntent = new Intent(UserProfileActivity.this,EmployerProfile.class);
                startActivity(employerIntent);
            }
        });

        txtEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employeeIntent = new Intent(UserProfileActivity.this,EmployeeProfile.class);
                startActivity(employeeIntent);
            }
        });
    }
}
