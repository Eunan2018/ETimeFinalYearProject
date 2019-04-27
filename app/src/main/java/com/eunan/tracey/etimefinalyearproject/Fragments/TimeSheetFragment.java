package com.eunan.tracey.etimefinalyearproject.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.MessageModel;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerTimeSheetActivity;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.payment.Payment;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryCalculator;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetBuilder;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetModel;
import com.eunan.tracey.etimefinalyearproject.timesheet.Weekday;
import com.eunan.tracey.etimefinalyearproject.upload.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.bean.customconverter.ConvertGermanToBoolean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class TimeSheetFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TimeSheetFragment";

    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday, btnSubmit, btnCancel;

    private TextView txtMondayDate, txtMondayDay, txtTuesdayDate, txtTuesdayDay, txtWednesdayDate,
            txtWednesdayDay, txtThursdayDate, txtThursdayDay, txtFridayDate, txtFridayDay;

    private EditText edtComments;
    private ProgressDialog progressDialog;
    @SuppressLint("SimpleDateFormat")
    private DateFormat dayDateFormat, displayDateFormat;
    private DatabaseReference timesheetRef, commentsRef;
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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
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
        commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "onCreateView: " + currentUserId);
        return view;
    }


    private void initialiseTextViewsAndButtons(View view) {
        btnMonday = view.findViewById(R.id.btn_monday);
        txtMondayDate = view.findViewById(R.id.text_view_monday_date);
        txtMondayDay = view.findViewById(R.id.text_view_monday_day);


        btnTuesday = view.findViewById(R.id.button_tuesday);
        txtTuesdayDate = view.findViewById(R.id.text_view_tuesday_date);
        txtTuesdayDay = view.findViewById(R.id.text_view_tuesday_day);


        btnWednesday = view.findViewById(R.id.button_wednesday);
        txtWednesdayDate = view.findViewById(R.id.text_view_wednesday_date);
        txtWednesdayDay = view.findViewById(R.id.text_view_wednesday_day);

        btnThursday = view.findViewById(R.id.button_thursday);
        txtThursdayDate = view.findViewById(R.id.text_view_thursday_date);
        txtThursdayDay = view.findViewById(R.id.text_view_thursday_day);


        btnFriday = view.findViewById(R.id.button_friday);
        txtFridayDate = view.findViewById(R.id.text_view_friday_date);
        txtFridayDay = view.findViewById(R.id.text_view_friday_day);


        edtComments = view.findViewById(R.id.edit_text_comments_ts);

        btnSubmit = view.findViewById(R.id.button_submit);
        btnCancel = view.findViewById(R.id.button_cancel_ts);

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

                if (TimeSheetBuilder.getTimeMap().size() < 1) {
                    progressDialog.cancel();
                    Toast.makeText(getContext(), "Please fill in all days.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Submit");
                    String msg = " days";
                    if(getDaysWorked(TimeSheetBuilder.getTimeMap()) == 1){
                        msg =" day";
                    }
                    alert.setMessage(String.valueOf( getDaysWorked(TimeSheetBuilder.getTimeMap()) + msg));
                    alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            uploadTimesheet();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
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

    private int getDaysWorked(LinkedHashMap<String, TimeSheetModel> timeMap) {
        int days = 0;
        for (Map.Entry<String, TimeSheetModel> pair : timeMap.entrySet()) {
            if (!pair.getValue().getProject().equals("ABSENT")) {
                days += 1;
            }
        }
        return days;

    }

    private void uploadTimesheet() {
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        }, 1000);
        String pushId = timesheetRef.push().getKey();
        resetButtonColour();
        timesheetRef.child(currentUserId).child(employerKey).child(pushId).setValue(TimeSheetBuilder.getTimeMap());
        MessageModel messageModel = new MessageModel(edtComments.getText().toString(), "");
        commentsRef.child(currentUserId).child(employerKey).child(pushId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TimeSheetBuilder.getTimeMap().clear();
                btnSubmit.setEnabled(false);
                btnSubmit.setBackground(getResources().getDrawable(R.drawable.round_button_gray));
                Toast.makeText(getContext(), "Cannot send another time-sheet for 20 minutes", Toast.LENGTH_SHORT).show();
                new CountDownTimer(20000, 10) { //Set Timer for 20 seconds
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getContext(), "Send again", Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                        btnSubmit.setBackground(getResources().getDrawable(R.drawable.round_button));
                    }
                }.start();
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

