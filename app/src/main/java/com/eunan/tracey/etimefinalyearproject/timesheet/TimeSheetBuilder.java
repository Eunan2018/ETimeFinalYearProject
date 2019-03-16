package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProjectModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeSheetBuilder extends AppCompatActivity {

    private static final String TAG = "TimeSheetBuilder";

    private EditText hours;
    private EditText comment;
    private Button btnDone;
    private RecyclerView recyclerView;

    // Firebase
    private DatabaseReference timesheetRef;
    private DatabaseReference projectRef;
    private DatabaseReference employerRef;
    private DatabaseReference assignedRef;
    private FirebaseUser currentUser;
    private String userId;
    public static Map<String, TimeSheetModel> timesheetMap = new HashMap<>();
    private TimeSheetModel timesheet;
    String projName;
    String projHours;
    String projComments;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_builder);

        hours = findViewById(R.id.edit_text_ts_hrs);
        comment = findViewById(R.id.edit_text_ts_comments);
        btnDone = findViewById(R.id.button_done_ts);
        recyclerView = findViewById(R.id.recycler_view_ts_builder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        timesheetRef = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");
        employerRef = FirebaseDatabase.getInstance().getReference().child("Employer");
        assignedRef = FirebaseDatabase.getInstance().getReference("EmployeeProjects");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = getIntent().getStringExtra("day");
                projHours = hours.getText().toString();
                projComments = comment.getText().toString();

                 timesheet = new TimeSheetModel(projName, projHours, projComments, day);

                timesheetMap.put(day, timesheet);

            }
        });
    }

    public static Map<String, TimeSheetModel> getTimeMap() {
        return timesheetMap;
    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("EmployeeProjects")
                .child(userId);
        FirebaseRecyclerOptions<EmployeeProjectModel> options =
                new FirebaseRecyclerOptions.Builder<EmployeeProjectModel>()
                        .setQuery(query, EmployeeProjectModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<EmployeeProjectModel, TimeSheetBuilder.EmployeeViewHolder>(options) {

            @Override
            public TimeSheetBuilder.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.project_layout_employee, parent, false);
                return new TimeSheetBuilder.EmployeeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final TimeSheetBuilder.EmployeeViewHolder employeeViewHolder, final int position, @NonNull EmployeeProjectModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String userId = getRef(position).getKey();

                assignedRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.getValue().toString();
                            employeeViewHolder.setName(name);
                            Log.d(TAG, "onDataChange: Winner " + ds.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                employeeViewHolder.setName(employee.getProject());
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String uName;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }


        public void setName(String name) {
            TextView userName = view.findViewById(R.id.textview_title);
            userName.setText(name);
            uName = name;
        }

        public String getName() {
            return uName;
        }
    }

}



