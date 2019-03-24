package com.eunan.tracey.etimefinalyearproject.employer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployerTimeSheetActivity extends AppCompatActivity {
    private final String TAG = "EmpTimeSheetActivity";


    // Firebase
    private DatabaseReference timesheetRef;
    private DatabaseReference historyRef;

    private String currentUser;
    private String employeeId;

    // Layout
    private Toolbar toolbar;
    private TextView txtTotal;
    private Button btnAccept;


    private Map<String,EmployerWeek> employerWeekMap;
    private List<EmployerWeek> employerWeekList;
    private EmployerWeek employerWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_employer);

        employerWeekMap = new HashMap<>();
        employerWeekList = new ArrayList<>();
        // Set the action bar and set the required name
        toolbar = findViewById(R.id.time_sheet_app_bar);
        txtTotal = findViewById(R.id.text_view_ts_total);
        btnAccept = findViewById(R.id.button_ts_accept);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this,R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        employeeId = getIntent().getStringExtra("employeeId");
        timesheetRef = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        timesheetRef.child(employeeId).child(currentUser).orderByPriority().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postChildSnap : childSnapshot.getChildren()) {
                        String day = postChildSnap.getKey();
                        String hours = postChildSnap.child("hours").getValue().toString();
                        String project = postChildSnap.child("project").getValue().toString();
                        Log.d(TAG, "onDataChange: Day: " + day + " Hours: " + " " + hours + " Project: " + project);
                        employerWeek = new EmployerWeek(day, hours, project);
                        employerWeekList.add(employerWeek);
                        employerWeekMap.put(day,employerWeek);
                    }
                }
                printTimeSheet(employerWeekList);
                Log.d(TAG, "onDataChange: employerWeekList: " + employerWeekList);
                Log.d(TAG, "onDataChange: size: " + employerWeekList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setFirstDayOfWeek(Calendar.MONDAY);
                c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE-MMM-d");
                final String date = dateFormat.format(c.getTime());

                Log.d(TAG, "onClick: friday: " + c.getTime());
                Log.d(TAG, "onClick: friday: " + dateFormat.format(c.getTime()));
                historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        historyRef.child(employeeId).child("Fri-Mar-8").setValue(employerWeekMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    private void printTimeSheet(List<EmployerWeek> employerWeekList) {
        int total = 0;

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (EmployerWeek week : employerWeekList) {
            builder.append(" ").append(week.getDay()).append("\t\t").append(week.getHours()).append("hrs").append("\t\t").append(week.getProjectt()).append("\n");
            total = total + Integer.valueOf(week.getHours());
        }
        builder.append("\n");
        builder.append(" ").append("Total Hours: ").append(total).append("hrs");

        Log.d(TAG, "printTimeSheet: \n" + builder.toString());
        txtTotal.setText(builder.toString());


    }

}

