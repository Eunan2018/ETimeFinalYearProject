package com.eunan.tracey.etimefinalyearproject.employer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.Fragments.EmployeeFragment;
import com.eunan.tracey.etimefinalyearproject.MessageModel;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.WriteExcel;
import com.eunan.tracey.etimefinalyearproject.employee.Employee;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.payment.Payment;
import com.eunan.tracey.etimefinalyearproject.project.ProjectActivity;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryActivity;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryCalculator;
import com.eunan.tracey.etimefinalyearproject.timesheet.Weekday;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployerTimeSheetActivity extends AppCompatActivity {
    private final String TAG = "EmpTimeSheetActivity";
    // WriteExcel writeExcel = new WriteExcel();


    // Firebase
    private DatabaseReference salaryRef;
    private DatabaseReference timesheetRef;
    private DatabaseReference historyRef;
    private DatabaseReference declineRef;
    private DatabaseReference paymentRef;
    private DatabaseReference commentsRef;
    private Button btnAttach, btnAccept, btnDecline;
    private TextView txtMon, txtMonProj, txtTues, txtTuesProj, txtWed, txtWedProj,
            txtThurs, txtThursProj, txtFri, txtFriProj,txtComments;
    private double rate = 0.0;
    private int code = 1;
    private int totalHrs;

    // Variables
    private String currentUser;
    private MessageModel messageModel;
    private double total;
    // Layout
    private Toolbar toolbar;
    private TextView txtTotal;

    private ProgressDialog progressDialog;
    private Map<String, EmployerWeek> employerWeekMap;
    private List<EmployerWeek> employerWeekList;
    private EmployerWeek employerWeek;
    private Payment payment;

    @Override
    protected void onStart() {
        super.onStart();
        salaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                rate = Double.parseDouble(dataSnapshot.child("hourlyRate").getValue().toString());
                code = 1;// Integer.parseInt(dataSnapshot.child("taxCode").getValue().toString());
                Log.d(TAG, "onDataChange: Rate: " + rate + " Code: " + code);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EmployerTimeSheetActivity.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
            }
        });
        total = SalaryCalculator.calculateSalary(totalHrs, rate, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_employer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialiseViews();
        employerWeekMap = new HashMap<>();
        employerWeekList = new ArrayList<>();
        btnAttach = findViewById(R.id.button_attach);
        // Set the action bar with name and logo
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialise dialog
        progressDialog = new ProgressDialog(EmployerTimeSheetActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Payment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        //  txtTotal = findViewById(R.id.text_view_ts_total);
        btnAccept = findViewById(R.id.button_ts_accept);
        btnDecline = findViewById(R.id.textview_ts_decline);

        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final String employeeId = getIntent().getStringExtra("id");
        timesheetRef = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        timesheetRef.keepSynced(true);
        commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        commentsRef.keepSynced(true);
        declineRef = FirebaseDatabase.getInstance().getReference().child("Decline");
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryTimesheet");
        paymentRef = FirebaseDatabase.getInstance().getReference().child("Payment");
        if (employeeId != null) {
            salaryRef = FirebaseDatabase.getInstance().getReference().child("Salary").child(currentUser).child(employeeId);

            btnAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EmployerTimeSheetActivity.this, EmpImage.class);
                    intent.putExtra("key1", currentUser);
                    intent.putExtra("key2", employeeId);
                    startActivity(intent);
                }
            });
            // firebase


            timesheetRef.child(employeeId).child(currentUser).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot postChildSnap : childSnapshot.getChildren()) {
                            String day = postChildSnap.getKey();
                            if (day.equals("A")) {
                                day = "Mon";
                            } else if (day.equals("B")) {
                                day = "Tue";
                            } else if (day.equals("C")) {
                                day = "Wed";
                            } else if (day.equals("D")) {
                                day = "Thu";
                            } else {
                                day = "Fri";
                            }
                            String hours = postChildSnap.child("hours").getValue().toString();
                            String project = postChildSnap.child("project").getValue().toString();
                            Log.d(TAG, "onDataChange: Day: " + day + " Hours: " + " " + hours + " Project: " + project);
                            employerWeek = new EmployerWeek(day, hours, project);
                            totalHrs += Integer.valueOf(hours);
                            employerWeekList.add(employerWeek);
                            employerWeekMap.put(day, employerWeek);
                        }
                    }
                    printTimeSheet(employerWeekList);
                    commentsRef.child(employeeId).child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                String comments = ds.child("message").getValue().toString();
                                txtComments.setText(comments);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onDataChange: employerWeekList: " + employerWeekList);
                    Log.d(TAG, "onDataChange: size: " + employerWeekList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    String error = databaseError.toString();
                    Toast.makeText(EmployerTimeSheetActivity.this, "Error retrieving time-sheet. Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCancelled: error" + error);
                }
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(EmployerTimeSheetActivity.this);

                    alert.setTitle("Payment");
                    alert.setMessage(String.valueOf("Total pay: £ " + String.valueOf(SalaryCalculator.calculateSalary(totalHrs, rate, code))));


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                }
                            }, 1000);
                            Calendar c = Calendar.getInstance();
                            c.setFirstDayOfWeek(Calendar.MONDAY);
                            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE-MMM-d", Locale.UK);
                            final String date = dateFormat.format(c.getTime());

                            Log.d(TAG, "onClick: friday: " + c.getTime());
                            Log.d(TAG, "onClick: friday: " + dateFormat.format(c.getTime()));
                            historyRef.child(employeeId).child(date).setValue(employerWeekMap);
                            payment = new Payment(date, String.valueOf("Total pay: £ " + SalaryCalculator.calculateSalary(totalHrs, rate, code)));
                            String pushId = paymentRef.push().getKey();
                            paymentRef.child(employeeId).child(pushId).setValue(payment);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert.show();


                    //  writeExcel.writeToExcel(employerWeekList);
                }
            });


            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(EmployerTimeSheetActivity.this);
                    final EditText edittext = new EditText(getApplicationContext());
                    alert.setTitle("Information");

                    alert.setView(edittext);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String message = edittext.getText().toString();
                            messageModel = new MessageModel(message, "default");
                            declineRef.child(employeeId).setValue(messageModel);
                          //  txtTotal.setText("");
                            Log.d(TAG, "onClick: text: " + message);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    alert.show();
                }
            });

            // android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            Intent intent = new Intent(this, EmployerProfileActivity.class);
            startActivity(intent);
        }

        // and this for killing app if we dont want to start

    }

    private void initialiseViews() {
        txtMon = findViewById(R.id.text_view_mon);
        txtMonProj = findViewById(R.id.text_view_proj_mon);
        txtTues = findViewById(R.id.text_view_tues);
        txtTuesProj = findViewById(R.id.text_view_tues_proj);
        txtWed = findViewById(R.id.text_view_wed);
        txtWedProj = findViewById(R.id.text_view_wed_proj);
        txtThurs = findViewById(R.id.text_view_thurs);
        txtThursProj = findViewById(R.id.text_view_thurs_proj);
        txtFri = findViewById(R.id.text_view_fri);
        txtFriProj = findViewById(R.id.text_view_fri_proj);
        txtTotal = findViewById(R.id.text_view_total);
        txtComments = findViewById(R.id.text_view_ts_comments);
        txtComments.setMovementMethod(new ScrollingMovementMethod());
    }


    private void printTimeSheet(List<EmployerWeek> employerWeekList) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (EmployerWeek week : employerWeekList) {
            if (week.getDay().equals("Mon")) {
                txtMon.setText(week.getHours());
                txtMonProj.setText(week.getProjectt());
            } else if (week.getDay().equals("Tue")) {
                txtTues.setText(week.getHours());
                txtTuesProj.setText(week.getProjectt());
            } else if (week.getDay().equals("Wed")) {
                txtWed.setText(week.getHours());
                txtWedProj.setText(week.getProjectt());
            } else if (week.getDay().equals("Thu")) {
                txtThurs.setText(week.getHours());
                txtThursProj.setText(week.getProjectt());
            } else {
                txtFri.setText(week.getHours());
                txtFriProj.setText(week.getProjectt());
            }


            builder.append(" ").append(week.getDay()).append("\t\t").append(week.getHours()).append("hrs").append("\t\t").append(week.getProjectt()).append("\n");
            total = total + Integer.valueOf(week.getHours());
        }
        builder.append("\n");
        builder.append(" ").append("Total: ").append(total).append("hrs");
        Log.d(TAG, "printTimeSheet: Rate: " + rate + " Code: " + code);

        Log.d(TAG, "printTimeSheet: \n" + builder.toString());
        //   txtTotal.setText(builder.toString());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}

