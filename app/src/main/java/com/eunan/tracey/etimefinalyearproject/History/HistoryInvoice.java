package com.eunan.tracey.etimefinalyearproject.History;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.invoice.InvoiceModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryInvoice extends AppCompatActivity {
    private static final String TAG = "HistoryInvoice";
    private DatabaseReference historyRef;
    private RecyclerView recyclerView;
    private String userId;
    // Layout
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_invoice);
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_invoice_history);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryInvoice.this));
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: starts");
        FirebaseRecyclerOptions<InvoiceModel> options =
                new FirebaseRecyclerOptions.Builder<InvoiceModel>()
                        .setQuery(historyRef.child(userId), InvoiceModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<InvoiceModel, HistoryViewHolder>(options) {

            @Override
            public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_layout, parent, false);
                return new HistoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final HistoryViewHolder holder, final int position, final InvoiceModel history) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String pushId = getRef(position).getKey();

                Log.d(TAG, "onBindViewHolder: key " + userId);

                if (pushId != null) {
                    historyRef.child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: starts");
                                String key = dataSnapshot.getKey();
                                Log.d(TAG, "onDataChange: key: " + key);
                                holder.setDate(key);
                            }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(HistoryInvoice.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(HistoryInvoice.this, "Error loading time-sheet. Try again later.", Toast.LENGTH_SHORT).show();
                }

                // TODO SAME AS HISTORY CSV
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date = holder.getDate();
                        final Intent intent = new Intent(HistoryInvoice.this, HistoryCSV.class);
                        intent.putExtra("date", date);
                        getApplicationContext().startActivity(intent);
                        Log.d(TAG, "onClick: date: " + date);
                    }
                });

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    /**
     * Inner class used because functionality only needed by TimeSheetActivity
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";

        View view;
        String tDate;
        ConstraintLayout constraintLayout;

        public HistoryViewHolder(View itemView) {
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
