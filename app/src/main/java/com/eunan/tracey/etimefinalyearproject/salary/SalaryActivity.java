package com.eunan.tracey.etimefinalyearproject.salary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.Fragments.EmployeeFragment;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SalaryActivity extends AppCompatActivity {
    private final String TAG = "SalaryActivity";
    // UI
    private TextView txtName;
    private EditText edtHrRate;
    private EditText edtTaxCode;
    private Button btnSetDetails;
    private RadioGroup rdoGroup;
    private RadioButton rdoTitle;
    private ProgressDialog progressDialog;

    // firebase
    private DatabaseReference salaryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_salary);

        // UI
        edtHrRate = findViewById(R.id.edit_text_salary_hr_rate2);
        btnSetDetails = findViewById(R.id.button_salary_details);
        rdoGroup = findViewById(R.id.radio_group_tax);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // retrieve employee name from intent
        //String name = getIntent().getStringExtra("name");
        final String employeeId = getIntent().getStringExtra("id");
        // set name
        // txtName.setText(name);

        btnSetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                int selectedId = rdoGroup.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: starts");
                if (String.valueOf(selectedId).equals("-1")) {
                    progressDialog.cancel();
                    Toast.makeText(SalaryActivity.this, "Need to select tax code", Toast.LENGTH_SHORT).show();
                } else {
                    // find the radiobutton by returned id
                    rdoTitle = findViewById(selectedId);
                    String title = rdoTitle.getText().toString();
                    String employeeHrRate = edtHrRate.getText().toString().trim();
                    SalaryModel salaryModel = new SalaryModel(employeeHrRate, title);
                    // Firebase
                    salaryRef = FirebaseDatabase.getInstance().getReference().child("Salary").child(currentUser).child(employeeId);
                    salaryRef.setValue(salaryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                }
                            },1000);
                            startActivity(new Intent(SalaryActivity.this, EmployerProfileActivity.class));
                            Toast.makeText(SalaryActivity.this, "Details have been saved", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

    }
}
