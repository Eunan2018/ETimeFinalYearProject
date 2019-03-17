package com.eunan.tracey.etimefinalyearproject.employer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.receipt.ReceiptModel;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryCalculator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class EmployerTimeSheetActivity extends AppCompatActivity {
    private final String TAG = "EmpTimeSheetActivity";

    interface CallBackListener {
        void onCallBackComplete(String project, double hrRate, double hrs, String taxCode, String email);
    }

    // Firebase
    private DatabaseReference root;
    private DatabaseReference salaryRef;
    private DatabaseReference empReceipt;

    private String key;
    private String employeeId;
    private String name;
    private String currentUser;
    private String hrs;
    private String taxCode;
    private String projectName;

    private String email;
    private double hrRate;
    // Layout
    private Toolbar toolbar;
    private TextView day;
    private TextView project;
    private TextView hour;
    private TextView comment;
    private Button decline;
    private Button accept;
    PrintWriter pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_employer);
        String filePath =  this.getFilesDir().getPath() + "output.csv";
        Log.d(TAG, "onCreate: " + filePath);
        try {
            pw = new PrintWriter(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Layout
        toolbar = findViewById(R.id.time_sheet_app_bar);
//        day = findViewById(R.id.text_view_ts_day);
//        project = findViewById(R.id.text_view_ts_total);
//        hour = findViewById(R.id.text_view_ts_hour);
//        comment = findViewById(R.id.text_view_ts_total);
//        decline = findViewById(R.id.button_ts_decline);
        accept = findViewById(R.id.button_ts_accept);

        // Set the action bar and set the required name
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve string values from EmployeeFragment when it is clicked clicked
        employeeId = getIntent().getStringExtra("ts_id");
        name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);

        // Firebase
        root = FirebaseDatabase.getInstance().getReference();
        salaryRef = FirebaseDatabase.getInstance().getReference();
        empReceipt = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        populateTimeSheet();

        // activate when clicked
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // call get salary and pass in interface callback to to retrieve the data
                getSalaryToCalculate(new CallBackListener() {
                    @Override
                    public void onCallBackComplete(String project, double hrRate, double hrs, String taxCode, String email) {

                        int code = Integer.parseInt(taxCode);
                        // return employees salary
                        double p = SalaryCalculator.calculateSalary(hrRate, hrs, code);
                        double basePay = hrRate * hrs;

                        String base = String.valueOf(basePay);
                        String hr = String.valueOf(hrs);
                        String r = String.valueOf(hrRate);
                        String pay = String.valueOf(p);


                        recipt(projectName, base, hr, r, pay);
                        Log.d(TAG, "onCallBackComplete: " + pay);
                        Toast.makeText(EmployerTimeSheetActivity.this, "Salary " + pay, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    // todo make interface to bring back hours and hourly rate

    public void getSalaryToCalculate(final CallBackListener callBackListener) {
        salaryRef.child("Salary").child(currentUser).child(employeeId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String hourlyRate = dataSnapshot.child("hourlyRate").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                taxCode = dataSnapshot.child("taxCode").getValue().toString();
                hrRate = Double.parseDouble(hourlyRate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        root.child("TimeSheet").child(employeeId).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hrs = dataSnapshot.child("hours").getValue().toString();
                projectName = dataSnapshot.child("project").getValue().toString();
                double hours = Double.parseDouble(hrs);
                callBackListener.onCallBackComplete(projectName, hrRate, hours, taxCode, email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateTimeSheet() {
        Log.d(TAG, "populateTimeSheet: starts");
        // Get to the second element in tree which is the employee id
        root.child("TimeSheet").child(employeeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the push key below the employeeId and use it to get values
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    key = db.getKey();
                }
                // Check if time-sheet is blank
                if (!TextUtils.isEmpty(key)) {
                    root.child("TimeSheet").child(employeeId).child(key).addValueEventListener(new ValueEventListener() {
                        // Populate time-sheet at employer's view
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: starts : " + dataSnapshot);
                            String days = dataSnapshot.child("day").getValue().toString();
                            String projects = dataSnapshot.child("project").getValue().toString();
                            String comments = dataSnapshot.child("comments").getValue().toString();
                            String hours = dataSnapshot.child("hours").getValue().toString();

                            // set text boxes
                            day.setText(days);
                            project.setText(projects);
                            comment.setText(comments);
                            hour.setText(hours);

                            // Enable buttons
                            decline.setEnabled(true);
                            decline.setVisibility(View.VISIBLE);
                            accept.setVisibility(View.VISIBLE);
                            accept.setEnabled(true);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String error = databaseError.toString();
                            Toast.makeText(EmployerTimeSheetActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // If time sheet is blank hide and disable buttons
                    decline.setEnabled(false);
                    decline.setVisibility(View.INVISIBLE);
                    accept.setVisibility(View.INVISIBLE);
                    accept.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.toString();
                Toast.makeText(EmployerTimeSheetActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recipt(final String project, final String hrs, final String rate, final String tax, final String pay) {

        final ReceiptModel receiptModel = new ReceiptModel(project, hrs, rate, pay, email);

        empReceipt = FirebaseDatabase.getInstance().getReference().child("Receipt").child(employeeId).push();
        empReceipt.setValue(receiptModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                StringBuilder builder = new StringBuilder();
                String ColumnNamesList = "PROJECT,HOURS";
                // No need give the headers Like: id, Name on builder.append
                builder.append(ColumnNamesList + "\n");
                builder.append(receiptModel.getProjectName() + ",");
                builder.append(receiptModel.getHrsWorked());
                builder.append('\n');

                pw.write(builder.toString());
                Log.d(TAG, "onSuccess: " + builder.toString());
                pw.close();
            }


        });

    }

}
