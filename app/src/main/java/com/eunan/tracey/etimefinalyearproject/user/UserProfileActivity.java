package com.eunan.tracey.etimefinalyearproject.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;

public class UserProfileActivity extends AppCompatActivity {

    private TextView employer;
    private TextView employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        employer = findViewById(R.id.textview_employer);
        employee = findViewById(R.id.textview_employee);


        employer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employerIntent = new Intent(UserProfileActivity.this,EmployerProfileActivity.class);
                startActivity(employerIntent);
            }
        });

        employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employeeIntent = new Intent(UserProfileActivity.this,EmployeeProfileActivity.class);
                startActivity(employeeIntent);
            }
        });
    }
}
