package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.Fragments.TimeSheetFragment;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProjectModel;
import com.eunan.tracey.etimefinalyearproject.payment.Payment;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryCalculator;
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
import com.google.firebase.database.collection.LLRBNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;


public class TimeSheetBuilder extends AppCompatActivity {

    private static final String TAG = "TimeSheetBuilder";


    private Button btnDone, btnAbsent, btnCancel;
    private RecyclerView recyclerView;
    private Spinner spinnerHours;
    private String hours;
    private boolean flag = false;
    //  private TextView txtMonday;
    private boolean inispinner;
    // Firebase
    private DatabaseReference assignedRef;
    private FirebaseUser currentUser;
    public static LinkedHashMap<String, TimeSheetModel> timesheetMap = new LinkedHashMap<>();
    private final ArrayList<Integer> selectionList = new ArrayList<>();
    private TimeSheetModel timesheet;
    private String projectName, day, userId;

    //Hour Spinner Values
    private String[] hoursArray = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_builder);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // hours = findViewById(R.id.edit_text_ts_hrs);

        btnDone = findViewById(R.id.button_done_ts);
        btnAbsent = findViewById(R.id.button_absent);
        btnCancel = findViewById(R.id.button_cancel_ts);
        //  txtMonday = findViewById(R.id.text_view_monday_day);
        recyclerView = findViewById(R.id.recycler_view_ts_builder);
        spinnerHours = findViewById(R.id.spinner_hours);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        assignedRef = FirebaseDatabase.getInstance().getReference("EmployeeProjects");

        btnAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerHours.getSelectedItem().toString().equals("0") && selectionList.get(0).equals(0)) {
                    btnAbsent.setBackground(getResources().getDrawable(R.drawable.round_button_gray));
                    flag = true;
                } else {
                    spinnerHours.setSelection(0);
                    Toast.makeText(TimeSheetBuilder.this, "Either absent or working.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerHours.setSelection(0);
                //btnAbsent.setBackgroundColor(.getColor(R.color.tw__composer_blue_text));
                btnAbsent.setBackground(getResources().getDrawable(R.drawable.round_button));
                flag = false;
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    hours = String.valueOf(0);
                    projectName = btnAbsent.getText().toString();
                    Log.d(TAG, "onClick: " + " Hours: " + hours + " " + "ProjectName: " + projectName);
                    flag = false;
                }
                day = getIntent().getStringExtra("day");
                if (day.equals(String.valueOf(Weekday.MONDAY))) {
                    day = "A";
                } else if (day.equals(String.valueOf(Weekday.TUESDAY))) {
                    day = "B";
                } else if (day.equals(String.valueOf(Weekday.WEDNESDAY))) {
                    day = "C";
                } else if (day.equals(String.valueOf(Weekday.THURSDAY))) {
                    day = "D";
                } else {
                    day = "E";
                }

                timesheet = new TimeSheetModel(projectName, hours);
                if (TextUtils.isEmpty(projectName)) {
                    Toast.makeText(TimeSheetBuilder.this, "Please select a project!!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(hours)) {
                    Toast.makeText(TimeSheetBuilder.this, "Please enter hours!!", Toast.LENGTH_SHORT).show();
                } else {
                    timesheetMap.put(day, timesheet);
                    finish();
                }
            }
        });

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, hoursArray);

        hoursAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinnerHours.setAdapter(hoursAdapter);

        spinnerHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!flag) {
                    if (!inispinner) {
                        inispinner = true;
                        return;
                    }
                    hours = spinnerHours.getSelectedItem().toString();
                    Log.d(TAG, "onItemSelected: spinner hours: " + hours);
//                btnAbsent.setBackgroundColor(getResources().getColor(R.color.tw__composer_blue_text));
//                flag = false;
                } else {
                    spinnerHours.setSelection(0);
                    Toast.makeText(TimeSheetBuilder.this, "Cannot be both absent and work", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(!timesheetMap.containsValue(day)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit? Nothing selected.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public static LinkedHashMap<String, TimeSheetModel> getTimeMap() {
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
            protected void onBindViewHolder(final TimeSheetBuilder.EmployeeViewHolder employeeViewHolder, final int position, @NonNull final EmployeeProjectModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                selectionList.add(0, 0);
                final String userId = getRef(position).getKey();


                if (userId != null) {
                    assignedRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot postChildSnap : dataSnapshot.getChildren()) {
                                String name = String.valueOf(postChildSnap.getValue());
                                employeeViewHolder.setName(name);
                                Log.d(TAG, "onDataChange: Winner " + postChildSnap.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError);
                            Toast.makeText(TimeSheetBuilder.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(TimeSheetBuilder.this, "Error userId is empty", Toast.LENGTH_SHORT).show();
                }


                employeeViewHolder.setName(employee.getProject());

                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectionList.get(0).equals(0)) {
                            if (!flag) {
                                    employeeViewHolder.view.setBackgroundColor(Color.GRAY);
                                    projectName = employeeViewHolder.getName();
                                    selectionList.set(0, 1);
                                } else {
                                    Toast.makeText(TimeSheetBuilder.this, "Cannot be ", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(TimeSheetBuilder.this, projectName + " already selected", Toast.LENGTH_SHORT).show();
                            }

                    }

                });
                employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectionList.set(0, 0);
                        employeeViewHolder.view.setBackgroundColor(Color.TRANSPARENT);
                        projectName = "";
                        return true;
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String pName;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }

        public void setName(String name) {
            TextView userName = view.findViewById(R.id.textview_title);
            userName.setText(name);
            pName = name;
        }

        public String getName() {
            return pName;
        }

    }

}



