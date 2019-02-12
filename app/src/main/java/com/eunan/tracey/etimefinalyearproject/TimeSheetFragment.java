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
import java.util.List;

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

    String projName;
    String projDay;
    String projHours;
    String projComments;
    String projid;


    // Firebase
    private DatabaseReference tsDatabaseReference;
    private DatabaseReference projDatabaseReference;
    private DatabaseReference assignedDatabaseReference;
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
        projDatabaseReference = FirebaseDatabase.getInstance().getReference().child("projects");
        assignedDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Assigned");
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

        assignedDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    for (DataSnapshot x : child.getChildren()) {
                        String val = x.getKey();
                        if ((val.equals(userId) && flag)){
                            flag = false;
                            setKey(key);
                        }
                    }
                }
            }

            void setKey(final String k) {
                projDatabaseReference.child(k).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String id = childSnapshot.getKey();
                            String projectName = dataSnapshot.child(id).child("projectName").getValue(String.class);
                            projList.add(projectName);
                            populateSpinner(projList);
                            Log.d(TAG, "onDataChange: starts " + projectName);
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
                        if (!TextUtils.isEmpty(k)) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("project").push();
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
