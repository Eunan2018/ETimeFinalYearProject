package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeSheetFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TimeSheetFragment";

    private Button btnMonday;
    private Button btnTuesday;
    private Button btnWednesday;
    private Button btnThursday;
    private Button btnFriday;

    private Button btnSubmit;
    private TextView txtMondayDate;
    private TextView txtMondayDay;
    private TextView txtMondayHours;

    private TextView txtTuesdayDate;
    private TextView txtTuesdayDay;
    private TextView txtTuesdayHours;

    private TextView txtWednesdayDate;
    private TextView txtWednesdayDay;
    private TextView txtWednesdayHours;

    private TextView txtThursdayDate;
    private TextView txtThursdayDay;
    private TextView txtThursdayHours;

    private TextView txtFridayDate;
    private TextView txtFridayDay;
    private TextView txtFridayHours;

    @SuppressLint("SimpleDateFormat")
    DateFormat dayDateFormat;
    @SuppressLint("SimpleDateFormat")
    DateFormat displayDateFormat;

    private DatabaseReference timesheetRef;
    private DatabaseReference employerRef;
    private String currentUserId;
    String employerKey;
    public TimeSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_timesheet, container, false);
        employerRef = FirebaseDatabase.getInstance().getReference("Employer");

        employerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    employerKey = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: employer key" + employerKey);
                    for(DataSnapshot postChildSnapshot : childSnapshot.getChildren()){
                        String employeeKey = postChildSnapshot.getKey();
                        if(TextUtils.equals(employeeKey,currentUserId)) {
                            Log.d(TAG, "onDataChange: employee key " + employeeKey + " currentUserId " + currentUserId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Layout

        btnMonday = view.findViewById(R.id.btn_monday);
        txtMondayDate = view.findViewById(R.id.text_view_monday_date);
        txtMondayDay = view.findViewById(R.id.text_view_monday_day);
        txtMondayHours = view.findViewById(R.id.text_view_moday_hours);

        btnTuesday = view.findViewById(R.id.button_tuesday);
        txtTuesdayDate = view.findViewById(R.id.text_view_tuesday_date);
        txtTuesdayDay = view.findViewById(R.id.text_view_tuesday_day);
        txtTuesdayHours = view.findViewById(R.id.text_view_tuesday_hours);

        btnWednesday = view.findViewById(R.id.button_wednesday);
        txtWednesdayDate = view.findViewById(R.id.text_view_wednesday_date);
        txtWednesdayDay = view.findViewById(R.id.text_view_wednesday_day);
        txtWednesdayHours = view.findViewById(R.id.text_view_wednesday_hrs);

        btnThursday = view.findViewById(R.id.button_thursday);
        txtThursdayDate = view.findViewById(R.id.text_view_thursday_date);
        txtThursdayDay = view.findViewById(R.id.text_view_thursday_day);
        txtThursdayHours = view.findViewById(R.id.text_view_thursday_hrs);

        btnFriday = view.findViewById(R.id.button_friday);
        txtFridayDate = view.findViewById(R.id.text_view_friday_date);
        txtFridayDay = view.findViewById(R.id.text_view_friday_day);
        txtFridayHours = view.findViewById(R.id.text_view_friday_hrs);

        btnSubmit = view.findViewById(R.id.button_submit);

        // Firebase
        timesheetRef = FirebaseDatabase.getInstance().getReference("TimeSheet");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "onCreateView: " + currentUserId);
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dayDateFormat = new SimpleDateFormat("dd");
        displayDateFormat = new SimpleDateFormat("EEE, MMM d");

        mondayBuilder();
        tuesdayBuilder();
        wednesdayBuilder();
        thursdayBuilder();
        fridayBuilder();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushId = timesheetRef.push().getKey();
                timesheetRef.child(currentUserId).child(employerKey).child(pushId).setValue(TimeSheetBuilder.getTimeMap())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext().getApplicationContext(), "In t builder", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void wednesdayBuilder() {
        Calendar wednesday = Calendar.getInstance();
        wednesday.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

        btnWednesday.setText(dayDateFormat.format(wednesday.getTime()));
        txtWednesdayHours.setText("0");
        txtWednesdayDay.setText(String.valueOf(Weekday.WEDNESDAY));
        txtWednesdayDate.setText(displayDateFormat.format(wednesday.getTime()));

        btnWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.WEDNESDAY));
                startActivity(intent);
            }
        });
    }

    private void thursdayBuilder() {
        Calendar thursday = Calendar.getInstance();
        thursday.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

        btnThursday.setText(dayDateFormat.format(thursday.getTime()));
        txtThursdayHours.setText("0");
        txtThursdayDay.setText(String.valueOf(Weekday.THURSDAY));
        txtThursdayDate.setText(displayDateFormat.format(thursday.getTime()));

        btnThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.THURSDAY));
                startActivity(intent);
            }
        });
    }

    private void fridayBuilder() {
        Calendar friday = Calendar.getInstance();
        friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        btnFriday.setText(dayDateFormat.format(friday.getTime()));
        txtFridayHours.setText("0");
        txtFridayDay.setText(String.valueOf(Weekday.FRIDAY));
        txtFridayDate.setText(displayDateFormat.format(friday.getTime()));

        btnFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.FRIDAY));
                startActivity(intent);
            }
        });
    }


}

