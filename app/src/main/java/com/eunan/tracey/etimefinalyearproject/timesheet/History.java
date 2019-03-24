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
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(History.this));
        historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        employerWeekList = new ArrayList<>();
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: starts");
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

                if(pushId != null) {
                    historyRef.child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: starts");
                            String key = dataSnapshot.getKey();
                            Log.d(TAG, "onDataChange: key: " + key);
                            employeeViewHolder.setDate(key);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(History.this, "Error loading time-sheet. Try again later.", Toast.LENGTH_SHORT).show();
                }

                employeeViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date = employeeViewHolder.getDate();
                        final Intent intent = new Intent(History.this, HistoryCSV.class);
                        intent.putExtra("date", date);
                        getApplicationContext().startActivity(intent);
                        Log.d(TAG, "onClick: date: " + date);
                    }
                });
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
            Log.d(TAG, "setDate: starts");
            TextView empDate = view.findViewById(R.id.history_date);
            empDate.setText(date);
            tDate = date;
            Log.d(TAG, "setDate: ends");
        }

        public String getDate() {
            Log.d(TAG, "getDate: starts");
            return tDate;
        }

    }

}
