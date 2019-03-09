package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class TimeSheetFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TimeSheetFragment";

    private Button btnMonday;
    private TextView txtMondayDate;
    private TextView txtMondayDay;
    private TextView txtMondayHours;

    @SuppressLint("SimpleDateFormat") DateFormat dayDateFormat;
    @SuppressLint("SimpleDateFormat") DateFormat displayDateFormat;


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
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dayDateFormat = new SimpleDateFormat("dd");
        displayDateFormat = new SimpleDateFormat("EEE, MMM d");

        mondayBuilder();
        //tuesdayBuilder();
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
                Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void tuesdayBuilder() {
//        Calendar tuesday = Calendar.getInstance();
//        tuesday.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
//
//        btnMonday.setText(dayDateFormat.format(tuesday.getTime()));
//        txtMondayHours.setText("0");
//        txtMondayDay.setText(String.valueOf(Weekday.TUESDAY));
//        txtMondayDate.setText(displayDateFormat.format(tuesday.getTime()));
//    }


//    private void populateSpinner(final List projList) {
//        Log.d(TAG, "populateSpinner: starts");
//        if (!projList.isEmpty()) {
//            ArrayAdapter<String> projects = new ArrayAdapter<String>(getContext().getApplicationContext(), android.R.layout.simple_spinner_item, projList);
//            projects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            tsSpinner.setAdapter(projects);
//        }
//        Log.d(TAG, "populateSpinner: ends ");
//
//        return;
//
//
//    }


}
