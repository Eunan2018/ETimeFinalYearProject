package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.FragmentCommunicator;
import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeSheetBuilder extends AppCompatActivity {


    private static final String TAG = "TimeSheetBuilder";

    private EditText hours;
    private EditText comment;
    private Spinner tsSpinner;
    private Button btnDone;
    TimeSheetModel timesheet;

    public static HashMap<String, TimeSheetModel> timesheetMap = new HashMap();

    String projName;
    String projHours;
    String projComments;
    String day;
    String employerKey;


    // Firebase
    private DatabaseReference tsDatabaseReference;
    private DatabaseReference projDatabaseReference;
    private DatabaseReference employeesDatabaseReference;
    private FirebaseUser currentUser;
    private String userId;
    private List<String> projList;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_builder);
        Intent i = getIntent();

        hours = findViewById(R.id.edit_text_ts_hrs);
        comment = findViewById(R.id.edit_text_ts_comments);
        tsSpinner = findViewById(R.id.spinner_time_sheet);
        btnDone = findViewById(R.id.button_done_ts);

        projList = new ArrayList<>();

        // Firebase
        tsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        projDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Projects");
        employeesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Employer");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getApplicationContext(), "Error current user is Null", Toast.LENGTH_SHORT).show();
        }

        /* Used to match the employer to the employee*/
        employeesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get the employer key from the employer table
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // set the employer key
                    employerKey = child.getKey();
                    // move down a node and loop through the employee keys
                    for (DataSnapshot postChild : child.getChildren()) {
                        // set the employee key
                        String employeeKey = postChild.getKey();
                        // if employeekey matches the userId, use employerKey to access employer projects
                        if ((employeeKey.equals(userId) && flag)) {
                            flag = false;
                            setKey(employerKey);
                        }
                    }
                }
            }

            void setKey(final String key) {
                projDatabaseReference.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // moves to second node of project table and gets push id
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            // set the push id
                            String pushId = childSnapshot.getKey();
                            // set the projectName
                            // TODO CHECK FOR NULL
                            String projectName = dataSnapshot.child(pushId).child("projectName").getValue(String.class);

                            ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>)
                                    dataSnapshot.child(pushId).child("userList").getValue();

                            // check if the list is empty
                            if (data != null) {
                                for (int i = 0; i < data.size(); i++) {
                                    // get position of the map
                                    Map<String, String> map = data.get(i);
                                    // check if the key matches the user id
                                    // and if it does add project to spinner
                                    if (map.containsKey(userId)) {
                                        projList.add(projectName);
                                        populateSpinner(projList);
                                    }
                                }
                                Log.d(TAG, "onDataChange: starts " + projectName);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String error = databaseError.toString();
                        Toast.makeText(getApplicationContext().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });

                tsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // TODO FIX FIRST ELEMENT IN SPINNER NOT TO SHOW
                        projName = parent.getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = getIntent().getStringExtra("day");
                projHours = hours.getText().toString();
                projComments = comment.getText().toString();

                final TimeSheetModel timesheet = new TimeSheetModel(projName, projHours, projComments);
                timesheetMap.put(day, timesheet);
                Log.d(TAG, "onClick: " + timesheet);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // savedInstanceState.putBundle("map",timesheetMap);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        timesheetMap.put(employerKey, timesheet);
    }

    public static Map<String, TimeSheetModel> getTimeMap() {
        return timesheetMap;
    }

    private void populateSpinner(final List projList) {
        Log.d(TAG, "populateSpinner: starts");
        if (!projList.isEmpty()) {
            ArrayAdapter<String> projects = new ArrayAdapter<String>(getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_item, projList);
            projects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tsSpinner.setAdapter(projects);
        }
        Log.d(TAG, "populateSpinner: ends ");

        return;


    }

}
