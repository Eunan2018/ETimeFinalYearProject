package com.eunan.tracey.etimefinalyearproject.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectActivity extends AppCompatActivity {
    private final String TAG = "ProjectModel Activity";
    // UI
    private EditText projectName,projectLocation;
    private Button createProject;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    // Firebase
    private DatabaseReference projectRef, userRef, employeeRef,assignedRef;
    // Class
    private String currentUserId;
    private Map<String, AssignedEmployess> employeesMap;
    private Map<String, AssignedEmployess> assignedEmployessMap;
    private ProgressDialog progressDialog;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_project);
        initialiseViews();
        progressDialog = new ProgressDialog(ProjectActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Onclick
        createProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProject();
            }
        });
    }

    private void initialiseViews() {
        employeesMap = new HashMap<>();
        assignedEmployessMap = new HashMap<>();
        // Firebase
        projectRef = FirebaseDatabase.getInstance().getReference("Projects");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        assignedRef = FirebaseDatabase.getInstance().getReference("EmployeeProjects");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        employeeRef = FirebaseDatabase.getInstance().getReference().child("Employer");

        // Layout
        createProject = findViewById(R.id.button_add_project);
        projectName = findViewById(R.id.edit_text_project_name);
        projectLocation = findViewById(R.id.edit_text_project_location);
        recyclerView = findViewById(R.id.project_employee_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));

        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addProject() {
        String name = projectName.getText().toString().trim();
        String location = projectLocation.getText().toString().trim();

        if (!validate(name)) {
            Toast.makeText(ProjectActivity.this, "Project Cannot Be Empty", Toast.LENGTH_LONG).show();
            projectName.findFocus();
        } else if (!validate(location)) {
            Toast.makeText(ProjectActivity.this, "Location Cannot Be Empty", Toast.LENGTH_LONG).show();
            projectLocation.findFocus();
        } else if (employeesMap.size() < 1) {
            Toast.makeText(this, "Please select an employee.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            // Create unique key for each project
            String id = projectRef.push().getKey();
            String assigned_push = assignedRef.push().getKey();
            ProjectModel project = new ProjectModel(name, location, (int) System.currentTimeMillis(), employeesMap);
            projectRef.child(currentUserId).child(id).setValue(project);
            for (Map.Entry<String, AssignedEmployess> entry : assignedEmployessMap.entrySet()) {
                EmployeeProjectModel employeeProjectModel = new EmployeeProjectModel(entry.getKey(), name);
                assignedRef.child(entry.getKey()).child(assigned_push).setValue(employeeProjectModel);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ProjectActivity.this, "Project Added", Toast.LENGTH_SHORT).show();
                    // clear all and reset focus
                    projectName.setText("");
                    projectLocation.setText("");
                    projectName.requestFocus();
                    progressDialog.cancel();
                    startActivity(new Intent(ProjectActivity.this, EmployerProfileActivity.class));
                    finish();
                }
            }, 1000);

            Log.d(TAG, "onClick: + " + project);
        }
    }

    public boolean validate(String data) {
        Log.d(TAG, "validate: starts " + data);
        if (!TextUtils.isEmpty(data)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: starts ");
        super.onStart();

        FirebaseRecyclerOptions<EmployeeModel> options =
                new FirebaseRecyclerOptions.Builder<EmployeeModel>()
                        .setQuery(employeeRef.child(currentUserId), EmployeeModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<EmployeeModel, ProjectActivity.EmployeeViewHolder>(options) {

            @Override
            public ProjectActivity.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new ProjectActivity.EmployeeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final EmployeeViewHolder employeeViewHolder, final int position, @NonNull final EmployeeModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String userId = getRef(position).getKey();

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumbImage").getValue().toString();
                        employeeViewHolder.setName(name);
                        employeeViewHolder.setKey(userId);
                        employeeViewHolder.setImage(ProjectActivity.this, image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String error = databaseError.toString();
                        Log.d(TAG, "onCancelled: " + error);
                    }
                });
                employeeViewHolder.setDate(employee.getDate());
                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: EmployeesMap size: " + employeesMap.size());
                        Log.d(TAG, "onClick: assignedEmployeesMap size: " + assignedEmployessMap.size());
                        addEmployeeToProject(employeeViewHolder);
                        Log.d(TAG, "onClick: EmployeesMap size: " + employeesMap.size());
                        Log.d(TAG, "onClick: assignedEmployeesMap size: " + assignedEmployessMap.size());
                    }
                });
                employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(TAG, "onLongClick: EmployeesMap size: " + employeesMap.size());
                        Log.d(TAG, "onLongClick: assignedEmployeesMap size: " + assignedEmployessMap.size());
                        removeEmployeeFromMap(employeeViewHolder);
                        Log.d(TAG, "onLongClick: EmployeesMap size: " + employeesMap.size());
                        Log.d(TAG, "onLongClick: assignedEmployeesMap size: " + assignedEmployessMap.size());
                        return true;
                    }
                });

            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void addEmployeeToProject(EmployeeViewHolder employeeViewHolder) {
        AssignedEmployess assignedEmployess = new AssignedEmployess(employeeViewHolder.getName(), employeeViewHolder.getKey());
        String key = employeeViewHolder.getKey();
        Log.d(TAG, "addEmployeeToProject: " + key);
        employeeViewHolder.view.setBackgroundColor(Color.GRAY);
        employeesMap.put(key, assignedEmployess);
        assignedEmployessMap.put(key, assignedEmployess);
        Log.d(TAG, "addEmployeeToProject: EmployeesMap size: " + employeesMap.size());
        Log.d(TAG, "addEmployeeToProject: assignedEmployeesMap size: " + assignedEmployessMap.size());
    }

    private void removeEmployeeFromMap(EmployeeViewHolder employeeViewHolder) {
        String key = employeeViewHolder.getKey();
        Log.d(TAG, "removeEmployeeFromProject: " + key);
        employeeViewHolder.view.setBackgroundColor(Color.TRANSPARENT);
        employeesMap.remove(key);   //put(employeeViewHolder.getKey(), assignedEmployess);
        assignedEmployessMap.remove(key);//     put(employeeViewHolder.getKey(),assignedEmployess);

        Log.d(TAG, "removeEmployeeFromProject: EmployeesMap size: " + employeesMap.size());
        Log.d(TAG, "removeEmployeeFromProject: assignedEmployeesMap size: " + assignedEmployessMap.size());
    }


    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String uName;
        String key;

        public EmployeeViewHolder(View itemView) {
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








