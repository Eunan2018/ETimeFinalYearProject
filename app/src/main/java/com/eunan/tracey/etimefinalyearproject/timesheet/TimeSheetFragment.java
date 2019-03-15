package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeSheetFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TimeSheetFragment";

    private Button btnMonday;
    private Button btnSubmit;
    private TextView txtMondayDate;
    private TextView txtMondayDay;
    private TextView txtMondayHours;
    private Button btnTuesday;
    private TextView txtTuesdayDate;
    private TextView txtTuesdayDay;
    private TextView txtTuesdayHours;

    @SuppressLint("SimpleDateFormat")
    DateFormat dayDateFormat;
    @SuppressLint("SimpleDateFormat")
    DateFormat displayDateFormat;

    private DatabaseReference databaseReferenceTimesheet;
    private String currentUserId;

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

        btnMonday = view.findViewById(R.id.btn_monday);
        txtMondayDate = view.findViewById(R.id.text_view_monday_date);
        txtMondayDay = view.findViewById(R.id.text_view_monday_day);
        txtMondayHours = view.findViewById(R.id.text_view_moday_hours);
        btnTuesday = view.findViewById(R.id.button_tuesday);
        txtTuesdayDate = view.findViewById(R.id.text_view_tuesday_date);
        txtTuesdayDay = view.findViewById(R.id.text_view_tuesday_day);
        txtTuesdayHours = view.findViewById(R.id.text_view_tuesday_hours);
        btnSubmit = view.findViewById(R.id.button_submit);

        // Firebase
        databaseReferenceTimesheet = FirebaseDatabase.getInstance().getReference("TimeSheet");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//

        dayDateFormat = new SimpleDateFormat("dd");
        displayDateFormat = new SimpleDateFormat("EEE, MMM d");

        mondayBuilder();
        tuesdayBuilder();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = databaseReferenceTimesheet.push().getKey();
                databaseReferenceTimesheet.child(currentUserId).child(id).setValue(TimeSheetBuilder.getTimeMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                    }
                });
                Log.d(TAG, "onClick: " + TimeSheetBuilder.getTimeMap());
            }
        });

    }

    private void mondayBuilder() {
        Calendar monday = Calendar.getInstance();
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        btnMonday.setText(dayDateFormat.format(monday.getTime()));
        txtMondayHours.setText("0");
        txtMondayDay.setText(String.valueOf(Weekday.MONDAY));
        txtMondayDate.setText(displayDateFormat.format(monday.getTime()));

        btnMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.MONDAY));
                startActivity(intent);
            }
        });
    }


    private void tuesdayBuilder() {
        Calendar tuesday = Calendar.getInstance();
        tuesday.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

        btnTuesday.setText(dayDateFormat.format(tuesday.getTime()));
        txtTuesdayHours.setText("0");
        txtTuesdayDay.setText(String.valueOf(Weekday.TUESDAY));
        txtTuesdayDate.setText(displayDateFormat.format(tuesday.getTime()));

        btnTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.TUESDAY));
                startActivity(intent);
            }
        });
    }


}
