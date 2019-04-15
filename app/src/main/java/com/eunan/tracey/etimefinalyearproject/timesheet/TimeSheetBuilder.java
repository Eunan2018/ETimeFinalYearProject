package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProjectModel;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;



public class TimeSheetBuilder extends AppCompatActivity {

    private static final String TAG = "TimeSheetBuilder";


    private Button btnDone;
    private RecyclerView recyclerView;
    private Spinner spinnerMinutes;
    private Spinner spinnerHours;
    private String hours;
    private String minutes;
    //  private TextView txtMonday;
    private boolean inispinner;
    // Firebase
    private DatabaseReference assignedRef;
    private FirebaseUser currentUser;
    private String userId;
    public static LinkedHashMap<String, TimeSheetModel> timesheetMap = new LinkedHashMap<>();
    private final ArrayList<Integer> selectionList = new ArrayList<>();
    private TimeSheetModel timesheet;
    private String projectName;

    private String day;

    //Hour Spinner Values
    private String[] hoursArray = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12"};

    //Minutes Spinner Values
    private String[] minutesArray = {"0", "15", "30", "45"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_builder);

        // hours = findViewById(R.id.edit_text_ts_hrs);

        btnDone = findViewById(R.id.button_done_ts);
      //  txtMonday = findViewById(R.id.text_view_monday_day);
        recyclerView = findViewById(R.id.recycler_view_ts_builder);
        spinnerHours = findViewById(R.id.spinner_hours);
        spinnerMinutes = findViewById(R.id.spinner_minutes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        assignedRef = FirebaseDatabase.getInstance().getReference("EmployeeProjects");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = getIntent().getStringExtra("day");

                timesheet = new TimeSheetModel(projectName, hours, minutes);
                if (TextUtils.isEmpty(projectName)) {
                    Toast.makeText(TimeSheetBuilder.this, "Please select a project!!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(hours, "0")) {
                    Toast.makeText(TimeSheetBuilder.this, "Please enter hours!!", Toast.LENGTH_SHORT).show();
               }else {
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
                if (!inispinner) {
                    inispinner = true;
                    return;
                }
                hours = spinnerHours.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, minutesArray);


        minutesAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinnerMinutes.setAdapter(minutesAdapter);

        spinnerMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!inispinner) {
                    inispinner = true;
                    return;
                }
                minutes = spinnerMinutes.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                if(userId != null){
                    assignedRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot postChildSnap : dataSnapshot.getChildren()) {
                                String name = String.valueOf(postChildSnap.getValue());
                                employeeViewHolder.setName(name);
                                Log.d(TAG, "onDataChange: Winner " +postChildSnap.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError);
                            Toast.makeText(TimeSheetBuilder.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(TimeSheetBuilder.this, "Error userId is empty", Toast.LENGTH_SHORT).show();
                }


                employeeViewHolder.setName(employee.getProject());

                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (selectionList.get(0).equals(0)) {
                            employeeViewHolder.view.setBackgroundColor(Color.GRAY);
                            projectName = employeeViewHolder.getName();
                            selectionList.set(0, 1);
                        } else {
                            Toast.makeText(TimeSheetBuilder.this, projectName + " already " +
                                    "selected. Select " + projectName + " again to cancel", Toast.LENGTH_SHORT).show();
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



