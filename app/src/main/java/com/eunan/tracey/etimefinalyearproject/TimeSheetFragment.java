package com.eunan.tracey.etimefinalyearproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSheetFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TimeSheetFragment";
    // Activity used for toast
    Activity activity = this.getActivity();
    // Layout
    private EditText project;
    private EditText day;
    private EditText hours;
    private EditText comment;
    private Button submit;
    private Spinner tsSpinner;
    private List<User> userList = new ArrayList<>();

    String projName;
    String projDay;
    String projHours;
    String projComments;
    String projid;


    // Firebase
    private DatabaseReference tsDatabaseReference;
    private DatabaseReference projDatabaseReference;
    private DatabaseReference employeesDatabaseReference;
    private FirebaseUser currentUser;
    private String userId;
    private List<String> projList;
    private ArrayAdapter tsAdapter;
    boolean flag = true;

    String userIdProject;
    String id;

    public TimeSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_timesheet, container, false);

        // Layout
        day = view.findViewById(R.id.edit_text_ts_day);
        hours = view.findViewById(R.id.edit_text_ts_hrs);
        comment = view.findViewById(R.id.edit_text_ts_comments);
        submit = view.findViewById(R.id.button_ts_submit);
        tsSpinner = view.findViewById(R.id.spinner_time_sheet);

        projList = new ArrayList<>();

        // Firebase
        tsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        projDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Projects");
        employeesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Employer");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(activity, "Error current user is Null", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        employeesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get the employer key
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String employerKey = child.getKey();
                    // move down a node and get the employee key
                    // if it matches userId, use employerKey to access employer projects
                    for (DataSnapshot postChild : child.getChildren()) {
                        String employeeKey = postChild.getKey();
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
                            String pushId = childSnapshot.getKey();
                            String projectName = dataSnapshot.child(pushId).child("projectName").getValue(String.class);

                            ArrayList<Map<String, String>> data  = ( ArrayList<Map<String, String>>) dataSnapshot.child(pushId).child("userList").getValue();

                            // check if the list is empty
                            if (!data.isEmpty()) {
                                for (int i = 0; i < data.size(); i++) {
                                    // get the map at position
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
                        Toast.makeText(getContext().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
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

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        projDay = TimeSheetFragment.this.day.getText().toString();
                        projHours = TimeSheetFragment.this.hours.getText().toString();
                        projComments = TimeSheetFragment.this.comment.getText().toString();

                        Timesheet timesheet = new Timesheet(projName, projHours, projComments, projDay);
                        if (!TextUtils.isEmpty(key)) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Projects").push();
                            String id = ref.getKey();
                            tsDatabaseReference.child(userId).child(id).setValue(timesheet).addOnSuccessListener(new OnSuccessListener<Void>() {

                                //TODO GET TOAST WORKING WITH THE FRAGMENT
                                @Override
                                public void onSuccess(Void aVoid) {
                                    comment.setText("");
                                    TimeSheetFragment.this.day.setText("");
                                    TimeSheetFragment.this.hours.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Id cannot be null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateSpinner(final List projList) {
        Log.d(TAG, "populateSpinner: starts");
        if (!projList.isEmpty()) {
            ArrayAdapter<String> projects = new ArrayAdapter<String>(getContext().getApplicationContext(), android.R.layout.simple_spinner_item, projList);
            projects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tsSpinner.setAdapter(projects);
        }
        Log.d(TAG, "populateSpinner: ends ");

        return;


    }


}
