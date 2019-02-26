package com.eunan.tracey.etimefinalyearproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployerTimeSheetActivity extends AppCompatActivity {
    private final String TAG = "EmpTimeSheetActivity";
    // Firebase
    private DatabaseReference root;
    private FirebaseAuth firebaseAuth;
//
//    // Intent values
//    private String ts_comments;
//    private String ts_projects;
//    private String ts_hours;
//    private String ts_days;
//    private String id;

    private String key;
    private String employeeId;
    private String name;
    // Layout
    private Toolbar toolbar;
    private TextView day;
    private TextView project;
    private TextView hour;
    private TextView comment;
    private Button decline;
    private Button accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_activity);

        // Layout
        toolbar = findViewById(R.id.time_sheet_app_bar);
        day = findViewById(R.id.text_view_ts_day);
        project = findViewById(R.id.text_view_ts_project);
        hour = findViewById(R.id.text_view_ts_hour);
        comment = findViewById(R.id.text_view_ts_comments);
        decline = findViewById(R.id.button_ts_decline);
        accept = findViewById(R.id.button_ts_accept);


        // Set the action bar and set the required name
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Retrieve string values from EmployeeFragment when it is clicked clicked

        // TODO CLEAN ALL THIS UP
//
//
//        ts_comments = getIntent().getStringExtra("ts_comments");
        employeeId = getIntent().getStringExtra("ts_id");
//        ts_days = getIntent().getStringExtra("ts_days");
//        ts_hours = getIntent().getStringExtra("ts_hour");
//        ts_projects = getIntent().getStringExtra("ts_proj ects");
        name = getIntent().getStringExtra("name");
//        id = getIntent().getStringExtra("ts_id");
        getSupportActionBar().setTitle(name);

        // Firebase
        root = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // Enable buttons
        decline.setEnabled(true);
        decline.setVisibility(View.VISIBLE);
        accept.setVisibility(View.VISIBLE);
        accept.setEnabled(true);


            // Get to the second element in tree which is the employee
            root.child("TimeSheet").child(employeeId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Get the push key below the employeeId and use it to get values
                    for (DataSnapshot db : dataSnapshot.getChildren()) {
                        key = db.getKey();
                    }
                    // Check if time-sheet is blank
                    if (!TextUtils.isEmpty(key)) {
                        root.child("TimeSheet").child(employeeId).child(key).addValueEventListener(new ValueEventListener() {
                            // Populate time-sheet at employer's view
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: starts : " + dataSnapshot);
                                String days = dataSnapshot.child("day").getValue().toString();
                                String projects = dataSnapshot.child("project").getValue().toString();
                                String comments = dataSnapshot.child("comments").getValue().toString();
                                String hours = dataSnapshot.child("hours").getValue().toString();

                                day.setText(days);
                                project.setText(projects);
                                comment.setText(comments);
                                hour.setText(hours);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                String error = databaseError.toString();
                                Toast.makeText(EmployerTimeSheetActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // If time sheet is blank hide and disable buttons
                        decline.setEnabled(false);
                        decline.setVisibility(View.INVISIBLE);
                        accept.setVisibility(View.INVISIBLE);
                        accept.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String error = databaseError.toString();
                    Toast.makeText(EmployerTimeSheetActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
    }

//
//
//        root.child(employeeId).child("-LY96SCWZLQJPguvloOk").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        root.child("TimeSheet").child(currentUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.hasChild(employeeId)){
//
//                    Map timeStampMap = new HashMap();
//                    timeStampMap.put("timestamp",ServerValue.TIMESTAMP);
//
//                    Map timeSheetMap = new HashMap();
//                    timeSheetMap.put("TimeSheet/" + currentUserId + "/" + employeeId,timeStampMap);
//                    timeSheetMap.put("TimeSheet/" + employeeId + "/" + currentUserId,timeStampMap);
//
//                    root.updateChildren(timeSheetMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//
//                            if(databaseError != null){
//                                Toast.makeText(EmployerTimeSheetActivity.this, "Error uploading time sheet", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendTimeSheet();
//            }
//        });


    private void loadMessages() {

    }

//    private void sendTimeSheet() {
//
//        String m = message.getText().toString();
//
//        if (!TextUtils.isEmpty(m)) {
//
//            String userRef = "message/" + currentUserId + "/" + employeeId;
//            String empRef = "message/" + employeeId + "/" + currentUserId;
//
//            DatabaseReference pushRef = root.child("messages").child(currentUserId)
//                    .child(employeeId).push();
//
//            String pushId = pushRef.getKey();
//
//            Map message = new HashMap();
//            message.put("message", m);
//            message.put("timestamp", ServerValue.TIMESTAMP);
//
//            Map timesheetMessageMap = new HashMap();
//            timesheetMessageMap.put(userRef + "/" + pushId, message);
//            timesheetMessageMap.put(empRef + "/" + pushId, message);
//
//            root.updateChildren(timesheetMessageMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                    if (databaseError != null) {
//                        Toast.makeText(EmployerTimeSheetActivity.this, "Error uploading time sheet", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//        message.setText("");
//    }


}
