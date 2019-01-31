package com.eunan.tracey.etimefinalyearproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TimeSheetFragment extends android.support.v4.app.Fragment {
    // Activity used for toast
    Activity activity = this.getActivity();
    // Layout
     EditText project;
     EditText day;
     EditText hours;
     EditText comment;
     Button submit;

    // Firebase
    DatabaseReference tsDatabaseReference;
    FirebaseUser currentUser;
    String userId;
    private View view;

    public TimeSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timesheet, container, false);

        // Layout
        project = view.findViewById(R.id.edit_text_ts_project);
        day = view.findViewById(R.id.edit_text_ts_day);
        hours = view.findViewById(R.id.edit_text_ts_hrs);
        comment = view.findViewById(R.id.edit_text_ts_comments);
        submit = view.findViewById(R.id.button_ts_submit);

        // Firebase
        tsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String project = TimeSheetFragment.this.project.getText().toString();
                String day = TimeSheetFragment.this.day.getText().toString();
                String hours = TimeSheetFragment.this.hours.getText().toString();
                String comments = comment.getText().toString();

                String id = tsDatabaseReference.push().getKey();
                Timesheet timesheet = new Timesheet(project,hours,comments,day);
                tsDatabaseReference.child(userId).child(id).setValue(timesheet).addOnSuccessListener(new OnSuccessListener<Void>() {


                    //TODO GET TOAST WORKING WITH THE FRAGMENT
                    @Override
                    public void onSuccess(Void aVoid) {
                        TimeSheetFragment.this.project.setText("");
                        comment.setText("");
                        TimeSheetFragment.this.day.setText("");
                        TimeSheetFragment.this.hours.setText("");
                        Toast.makeText(getContext(), "TimeSheet successfully added", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


}
