package com.eunan.tracey.etimefinalyearproject.History;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerWeek;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryCSV extends AppCompatActivity {

    private final static String TAG = "HistoryCSV";

    // Firebase
    private DatabaseReference historyRef;
    private String currentUserId;

    // Layout
    private TextView txtTotal;
    private Map<String,EmployerWeek> employerWeekMap;
    private List<EmployerWeek> employerWeekList;
    private EmployerWeek employerWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_history_csv);
        final String date = getIntent().getStringExtra("date");
        employerWeekMap = new HashMap<>();
        employerWeekList = new ArrayList<>();
        txtTotal = findViewById(R.id.text_view_total_hs_csv);

        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryTimesheet");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        historyRef.child(currentUserId).orderByKey(). addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postChildSnap : childSnapshot.getChildren()) {
                        if(TextUtils.equals(childSnapshot.getKey(),date)) {
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
