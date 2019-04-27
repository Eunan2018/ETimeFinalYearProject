package com.eunan.tracey.etimefinalyearproject.History;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerWeek;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployerTimeSheetHist extends AppCompatActivity {

    private final static String TAG = "DisplayHistoryEmployee";

    // Firebase
    private DatabaseReference historyRef;
    private String currentUserId,employeeId;
    // Layout
    private Toolbar toolbar;
    // Layout
    private TextView txtTotal;
    private Map<String, EmployerWeek> employerWeekMap;
    private List<EmployerWeek> employerWeekList;
    private EmployerWeek employerWeek;
    private TextView txtMon, txtMonProj, txtTues, txtTuesProj, txtWed, txtWedProj,
            txtThurs, txtThursProj, txtFri, txtFriProj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_history_csv);
        // Set the action bar with name and logo
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String date = getIntent().getStringExtra("date");
        employerWeekMap = new HashMap<>();
        employerWeekList = new ArrayList<>();
        employeeId = getIntent().getStringExtra("employeeId");
        initialiseViews();
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryEmployerTimesheet");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        historyRef.child(currentUserId).child(employeeId).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postChildSnap : childSnapshot.getChildren()) {
                        if (TextUtils.equals(childSnapshot.getKey(), date)) {
                            String day = postChildSnap.getKey();
                            String hours = String.valueOf(postChildSnap.child("hours").getValue());
                            String project = String.valueOf(postChildSnap.child("projectt").getValue());
                            Log.d(TAG, "onDataChange: Day: " + day + " Hours: " + " " + hours + " Project: " + project);
                            employerWeek = new EmployerWeek(day, hours, project);
                            employerWeekList.add(employerWeek);
                            employerWeekMap.put(day, employerWeek);
                        }
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


        Log.d(TAG, "onDataChange: employerWeekList: " + employerWeekList);
        Log.d(TAG, "onDataChange: size: " + employerWeekList.size());
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
        }
        Log.d(TAG, "printTimeSheet: \n" + builder.toString());
        //   txtTotal.setText(builder.toString());

    }


}
