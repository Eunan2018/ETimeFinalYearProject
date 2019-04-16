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
    private EditText edtHrRate;
    private EditText edtTaxCode;
    private Button btnSetDetails;

    // firebase
    private DatabaseReference salaryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_salary);

        // layout
        txtName = findViewById(R.id.text_view_salary_name);
        edtHrRate = findViewById(R.id.edit_text_salary_hr_rate2);
        edtTaxCode = findViewById(R.id.edit_text_salary_tax_code);
        btnSetDetails = findViewById(R.id.button_salary_details);
        // retrieve employee name from intent
        String name = getIntent().getStringExtra("name");
        final String employeeId = getIntent().getStringExtra("id");
        // set name
        txtName.setText(name);

        // firebase
        // get the current user's key
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        btnSetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starts");

                String employeeHrRate = edtHrRate.getText().toString().trim();
                String employeeTaxCode = edtTaxCode.getText().toString().trim();

                SalaryModel salaryModel = new SalaryModel( employeeHrRate, employeeTaxCode);

                // firebase 
                salaryRef = FirebaseDatabase.getInstance().getReference().child("Salary").child(currentUser).child(employeeId);
                salaryRef.setValue(salaryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SalaryActivity.this, "Details have been saved", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
