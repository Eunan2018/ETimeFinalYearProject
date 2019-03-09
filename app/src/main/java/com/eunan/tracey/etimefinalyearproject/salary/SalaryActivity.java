package com.eunan.tracey.etimefinalyearproject.salary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SalaryActivity extends AppCompatActivity {
    private final String TAG = "SalaryActivity";
    // layout
    private TextView txtName;
    private EditText edtEmail;
    private EditText edtHrRate;
    private EditText edtNINum;
    private EditText edtTaxCode;
    private Button btnSetDetails;

    // firebase
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_salary);

        // layout
        txtName = findViewById(R.id.text_view_salary_name);
        edtEmail = findViewById(R.id.edit_text_salary_email);
        edtHrRate = findViewById(R.id.edit_text_salary_hr_rate2);
        edtNINum = findViewById(R.id.edit_text_salary_ni_num);
        edtTaxCode = findViewById(R.id.edit_text_salary_tax_code);
        btnSetDetails = findViewById(R.id.button_salary_details);
        // retrieve employee name from intent
        String name = getIntent().getStringExtra("name");
        final String employeeId = getIntent().getStringExtra("ts_id");
        // set name
        txtName.setText(name);

        // firebase
        // get the current user's key
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        btnSetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");
                String employeeName = txtName.getText().toString().trim();
                String employeeEmail = edtEmail.getText().toString().trim();
                String employeeHrRate = edtHrRate.getText().toString().trim();
                String employeeNiNum = edtNINum.getText().toString().trim();
                String employeeTaxCode = edtTaxCode.getText().toString().trim();

                SalaryModel salaryModel = new SalaryModel(employeeName, employeeEmail, employeeHrRate, employeeTaxCode, employeeNiNum);

                // firebase 
                root = FirebaseDatabase.getInstance().getReference().child("Salary").child(currentUser).child(employeeId);
                root.setValue(salaryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SalaryActivity.this, "Details have been saved", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
