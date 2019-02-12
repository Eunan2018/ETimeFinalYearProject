package com.eunan.tracey.etimefinalyearproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSheetActvity extends AppCompatActivity {

    // Firebase
    private DatabaseReference root;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private String employeeId;


    // Layout
    private Toolbar toolbar;
    private ImageButton add;
    private ImageButton send;
    private EditText message;
    private TextView day;
    private TextView project;

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TimeSheetAdapter timeSheetAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_actvity);

        // Layout
        toolbar = findViewById(R.id.time_sheet_app_bar);
//        add = findViewById(R.id.image_button_add);
//        send = findViewById(R.id.image_button_send);
//        message = findViewById(R.id.edit_text_message);
        day = findViewById(R.id.text_view_ts_day);
        project = findViewById(R.id.text_view_ts_project);
        linearLayoutManager = new LinearLayoutManager(this);

//        recyclerView = findViewById(R.id.recycler_view_timesheet);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        timeSheetAdapter = new TimeSheetAdapter(messageList);
//        recyclerView.setAdapter(timeSheetAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);

        // Firebase
        root = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        employeeId = getIntent().getStringExtra("from_user_id");

        root.child("TimeSheet").child(employeeId).child("-LXzlUKOYCqOdbXWjvgC").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String days = dataSnapshot.child("day").getValue().toString();
                String projects = dataSnapshot.child("project").getValue().toString();

                String x = dataSnapshot.child("comments").getValue().toString();
                String y = dataSnapshot.child("hours").getValue().toString();
                day.setText(days);
                project.setText(projects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
//                                Toast.makeText(TimeSheetActvity.this, "Error uploading time sheet", Toast.LENGTH_SHORT).show();
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
    }

    private void loadMessages() {

    }

    private void sendTimeSheet() {

        String m = message.getText().toString();

        if(!TextUtils.isEmpty(m)){

            String userRef = "message/" + currentUserId + "/" + employeeId;
            String empRef = "message/" + employeeId + "/" + currentUserId;

            DatabaseReference pushRef = root.child("messages").child(currentUserId)
                    .child(employeeId).push();

            String pushId = pushRef.getKey();

            Map message  = new HashMap();
            message.put("message",m);
            message.put("timestamp",ServerValue.TIMESTAMP);

            Map timesheetMessageMap = new HashMap();
            timesheetMessageMap.put(userRef + "/" + pushId, message);
            timesheetMessageMap.put(empRef + "/" + pushId, message);

            root.updateChildren(timesheetMessageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Toast.makeText(TimeSheetActvity.this, "Error uploading time sheet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        message.setText("");
    }


}
