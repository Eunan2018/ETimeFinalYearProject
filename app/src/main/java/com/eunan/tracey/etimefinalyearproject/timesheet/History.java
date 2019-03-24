package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerWeek;
import com.eunan.tracey.etimefinalyearproject.project.MaintainProject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    private final static String TAG = "History";

    private DatabaseReference historyRef;
    private RecyclerView recyclerView;
    private List<EmployerWeek> employerWeekList;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(History.this));
        historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        employerWeekList = new ArrayList<>();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<HistoryModel> options =
                new FirebaseRecyclerOptions.Builder<HistoryModel>()
                        .setQuery(historyRef.child(userId), HistoryModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<HistoryModel, EmployeeViewHolder>(options) {

            @Override
            public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_layout, parent, false);
                return new EmployeeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final EmployeeViewHolder employeeViewHolder, final int position, final HistoryModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                 final String pushId = getRef(position).getKey();

                Log.d(TAG, "onBindViewHolder: key " + userId);



                        historyRef.child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getKey();
                                Log.d(TAG, "onDataChange: key: " + key);
                                employeeViewHolder.setDate(key);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        employeeViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String date = employeeViewHolder.getDate();
                                final Intent intent = new Intent(History.this, HistoryCSV.class);
                                intent.putExtra("date", date);
                                getApplicationContext().startActivity(intent);
                                Toast.makeText(History.this, "hello", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onClick: date: " + date);
                            }
                        });
//                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(History.this, HistoryCSV.class);
//                        intent.putExtra("project", itemList.getProjectName());
//                        intent.putExtra("location", itemList.getProjectLocation());
//                        intent.putExtra("timestamp",itemList.getProjectTimestamp());
//                        context.startActivity(intent);
//                        Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
//                    }
//                });
                employeeViewHolder.setDate(employee.getDay());
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String tDate;
        ConstraintLayout constraintLayout;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
            this.constraintLayout = itemView.findViewById(R.id.history_view);
        }

        public void setDate(String date) {
            TextView empDate = view.findViewById(R.id.history_date);
            empDate.setText(date);
            tDate = date;
        }

        public String getDate() {
           return tDate;
        }

    }

}
