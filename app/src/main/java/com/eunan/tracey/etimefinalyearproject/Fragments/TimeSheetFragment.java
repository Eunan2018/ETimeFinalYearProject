package com.eunan.tracey.etimefinalyearproject.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetBuilder;
import com.eunan.tracey.etimefinalyearproject.timesheet.Weekday;
import com.eunan.tracey.etimefinalyearproject.upload.UploadActivity;
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

    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday, btnSubmit, btnCancel;

    private TextView txtMondayDate, txtMondayDay, txtMondayHours, txtTuesdayDate, txtTuesdayDay, txtTuesdayHours, txtWednesdayDate,
            txtWednesdayDay, txtWednesdayHours, txtThursdayDate, txtThursdayDay, txtThursdayHours, txtFridayDate, txtFridayDay, txtFridayHours;


    @SuppressLint("SimpleDateFormat")
    private DateFormat dayDateFormat, displayDateFormat;

    private DatabaseReference timesheetRef;
    private ImageView imageView;
    private String currentUserId, employerKey;

    private TextClock textClock;

    public TimeSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_timesheet, container, false);
        DatabaseReference employerRef = FirebaseDatabase.getInstance().getReference("Employer");
        imageView = view.findViewById(R.id.image_view_upload);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadActivity.class);
                intent.putExtra("key2", currentUserId);
                intent.putExtra("key1", employerKey);
                startActivity(intent);

            }
        });
        textClock = new TextClock(getContext());

        employerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    employerKey = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: employer key" + employerKey);
                    for (DataSnapshot postChildSnapshot : childSnapshot.getChildren()) {
                        String employeeKey = postChildSnapshot.getKey();
                        if (TextUtils.equals(employeeKey, currentUserId)) {
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
            }
        });

        // Layout
        initialiseTextViewsAndButtons(view);

        // Firebase
        timesheetRef = FirebaseDatabase.getInstance().getReference("TimeSheet");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "onCreateView: " + currentUserId);
        return view;
    }


    private void initialiseTextViewsAndButtons(View view) {
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
        btnCancel = view.findViewById(R.id.button_cancel_ts);


        // textClock.setFormat24Hour("HH:mm:ss");
        textClock.setText(textClock.getText().toString());
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
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
                if (TimeSheetBuilder.getTimeMap().size() < 5) {
                    Toast.makeText(getContext(), "Please fill in all days.", Toast.LENGTH_SHORT).show();
                } else {
                    String pushId = timesheetRef.push().getKey();
                    resetButtonColour();
                    timesheetRef.child(currentUserId).child(employerKey).child(pushId).setValue(TimeSheetBuilder.getTimeMap());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonColour();
                int size = TimeSheetBuilder.getTimeMap().size();
                Log.d(TAG, "onClick: size 1: " + size);
                TimeSheetBuilder.getTimeMap().clear();
                size = TimeSheetBuilder.getTimeMap().size();
                Log.d(TAG, "onClick: size 2: " + size);
            }
        });
    }

    private void resetButtonColour() {
        btnMonday.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
        btnTuesday.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
        btnWednesday.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
        btnThursday.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
        btnFriday.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
    }

    private void mondayBuilder() {
        Calendar monday = Calendar.getInstance();
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        btnMonday.setText(dayDateFormat.format(monday.getTime()));
        txtMondayHours.setText("0");
        txtMondayDay.setText(String.valueOf(Weekday.MONDAY));
        txtMondayDate.setText(displayDateFormat.format(monday.getTime()));

        txtMondayDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnMonday.setBackgroundColor(Color.GRAY);
                    }
                }, 1000);
                Intent intent = new Intent(getContext(), TimeSheetBuilder.class);
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

        txtTuesdayDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnTuesday.setBackgroundColor(Color.GRAY);
                    }
                }, 1000);
                Intent intent = new Intent(getContext(), TimeSheetBuilder.class);
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

        txtWednesdayDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnWednesday.setBackgroundColor(Color.GRAY);
                    }
                }, 1000);
                Intent intent = new Intent(getContext(), TimeSheetBuilder.class);
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

        txtThursdayDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnThursday.setBackgroundColor(Color.GRAY);
                    }
                }, 1000);
                Intent intent = new Intent(getContext(), TimeSheetBuilder.class);
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

        txtFridayDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnFriday.setBackgroundColor(Color.GRAY);
                    }
                }, 1000);
                Intent intent = new Intent(getContext(), TimeSheetBuilder.class);
                intent.putExtra("day", String.valueOf(Weekday.FRIDAY));
                startActivity(intent);
            }
        });
    }


}

