package com.eunan.tracey.etimefinalyearproject.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.AssignedEmployess;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProjectModel;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEmployeeActivity extends AppCompatActivity {
    private final String TAG = "AddEmployeeActivity";
    // UI
    private Button updateProject;
    private RecyclerView recyclerView;

    // Firebase
    private DatabaseReference projectRef, userRef, employerRef, employeeRef;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    // Variables
    private String currentUserId;
    private int timestamp;
    private HashMap<String, Object> assignedEmployessList;
    AssignedEmployess assignedEmployess;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignedEmployessList = new HashMap<>();
        assignedEmployess = new AssignedEmployess();
        // Initialise dialog
        progressDialog = new ProgressDialog(AddEmployeeActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setTitle("Update Project");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        // Firebase
        projectRef = FirebaseDatabase.getInstance().getReference("Projects");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        employerRef = FirebaseDatabase.getInstance().getReference().child("Employer");
        employeeRef = FirebaseDatabase.getInstance().getReference().child("EmployeeProjects");
        updateProject = findViewById(R.id.button_update_add_employee);
        recyclerView = findViewById(R.id.recycler_view_add_employee);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddEmployeeActivity.this));
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: starts ");
        super.onStart();
        timestamp = getIntent().getIntExtra("time", 0);
        FirebaseRecyclerOptions<EmployeeModel> options =
                new FirebaseRecyclerOptions.Builder<EmployeeModel>()
                        .setQuery(employerRef.child(currentUserId), EmployeeModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<EmployeeModel, AddEmployeeActivity.AddEmployeeViewHolder>(options) {

            @Override
            public AddEmployeeActivity.AddEmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new AddEmployeeActivity.AddEmployeeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final AddEmployeeActivity.AddEmployeeViewHolder employeeViewHolder, final int position, @NonNull final EmployeeModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String userId = getRef(position).getKey();

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumbImage").getValue().toString();
                        employeeViewHolder.setName(name);
                        employeeViewHolder.setKey(userId);
                        employeeViewHolder.setImage(AddEmployeeActivity.this, image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddEmployeeActivity.this, "Unable to add employee. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
                employeeViewHolder.setDate(employee.getDate());
                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AssignedEmployess assignedEmployess = new AssignedEmployess(employeeViewHolder.getName(), employeeViewHolder.getKey());
                        employeeViewHolder.view.setBackgroundColor(Color.GRAY);
                        Log.d(TAG, "removeEmployeeFromProject: assignedList size: size: " + assignedEmployessList.size());
                        assignedEmployessList.put(employeeViewHolder.getKey(), assignedEmployess);
                        Log.d(TAG, "removeEmployeeFromProject: assignedList size: size: " + assignedEmployessList.size());
                    }

                });

                employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String key = employeeViewHolder.getKey();
                        Log.d(TAG, "removeEmployeeFromProject: " + key);
                        Log.d(TAG, "removeEmployeeFromProject: assignedList size: size: " + assignedEmployessList.size());
                        employeeViewHolder.view.setBackgroundColor(Color.TRANSPARENT);
                        assignedEmployessList.remove(key);
                        Log.d(TAG, "removeEmployeeFromProject: assignedList size: " + assignedEmployessList.size());
                        return true;
                    }
                });


                    updateProject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (assignedEmployessList.size() >= 1) {
                                progressDialog.show();
                                projectRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String key = ds.getKey();
                                            String time = ds.child("projectTimestamp").getValue().toString();
                                            final String project = ds.child("projectName").getValue().toString();
                                            if (time.equals(String.valueOf(timestamp))) {
                                                projectRef.child(currentUserId).child(key).child("assignedEmployessList").updateChildren(assignedEmployessList);
                                                EmployeeProjectModel employeeProjectModel =
                                                        new EmployeeProjectModel(userId, project);
                                                String id = employeeRef.push().getKey();
                                                employeeRef.child(userId).child(id).setValue(employeeProjectModel);
                                                assignedEmployessList.clear();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.cancel();
                                                        startActivity(new Intent(AddEmployeeActivity.this, EmployerProfileActivity.class));
                                                        finish();
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: " + String.valueOf(databaseError));
                                    }
                                });
                            }else{
                                Toast.makeText(AddEmployeeActivity.this, "Need to add an employee", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class AddEmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String uName;
        String key;

        public AddEmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }

        public void setDate(String date) {
            TextView empDate = view.findViewById(R.id.user_single_status);
            empDate.setTextSize(8.f);
            empDate.setText(date);
        }

        public void setName(String name) {
            TextView userName = view.findViewById(R.id.user_single_name);
            userName.setText(name);
            uName = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String k) {

            key = k;
        }

        public String getName() {
            return uName;
        }

        public void setImage(Context context, String image) {
            CircleImageView circleImageView = view.findViewById(R.id.history_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }

    }
}
