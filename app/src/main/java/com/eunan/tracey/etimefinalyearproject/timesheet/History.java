package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History extends AppCompatActivity {

    private final static String TAG = "History";

    private DatabaseReference historyRef;
    private RecyclerView recyclerView;
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
                employeeViewHolder.setDate(employee.getDay());
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }

        public void setDate(String date) {
            TextView empDate = view.findViewById(R.id.history_date);
            empDate.setText(date);
        }

    }

}
