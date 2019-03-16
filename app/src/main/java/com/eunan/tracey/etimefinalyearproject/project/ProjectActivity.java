package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.AssignedEmployess;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.Employee;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
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
    // Layout
    private EditText projectName;
    private EditText projectLocation;
    private EditText projectDescription;
    private Button addProject;
    private RecyclerView recyclerView;

    // Database
    private DatabaseReference projectRef;
    private DatabaseReference userRef;
    private DatabaseReference employeeRef;
    private DatabaseReference assignedRef;
    private FirebaseUser currentUser;

    // Variables
    private String currentUserId;
    private Map<String, AssignedEmployess> assignedEmployessList;
    private Map<String, String> employeeProjects;
    Employee employee;
    AssignedEmployess assignedEmployess;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_project);


        assignedEmployessList = new HashMap();
        employeeProjects = new HashMap();
        assignedEmployess = new AssignedEmployess();
        // Firebase
        projectRef = FirebaseDatabase.getInstance().getReference("Projects");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        assignedRef = FirebaseDatabase.getInstance().getReference("Employee");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        employeeRef = FirebaseDatabase.getInstance().getReference().child("Employer");

        // Layout
        addProject = findViewById(R.id.button_add_project);
        projectName = findViewById(R.id.edit_text_project_name);
        projectDescription = findViewById(R.id.edit_text_project_description);
        projectLocation = findViewById(R.id.edit_text_project_location);
        recyclerView = findViewById(R.id.project_employee_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));

        // Onclick
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = projectName.getText().toString().trim();
                String location = projectLocation.getText().toString().trim();
                String description = projectDescription.getText().toString().trim();
                if (!validate(name)) {
                    Toast.makeText(ProjectActivity.this, "Project Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(location)) {
                    Toast.makeText(ProjectActivity.this, "Location Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(description)) {
                    Toast.makeText(ProjectActivity.this, "Description Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else {
                    // Create unique key for each project
                    String id = projectRef.push().getKey();
                    ProjectModel project = new ProjectModel(name, location, description, (int) System.currentTimeMillis(), assignedEmployessList);
                    projectRef.child(currentUserId).child(id).setValue(project);
                    assignedRef.child(name).setValue(assignedEmployess);
                    Log.d(TAG, "onClick: + " + project);
                    Toast.makeText(ProjectActivity.this, "Project Added", Toast.LENGTH_SHORT).show();
                    // clear all and reset focus
                    projectName.setText("");
                    projectLocation.setText("");
                    projectDescription.setText("");

                    // empMap.clear();
                    // employeeAdapter.notifyDataSetChanged();
                    projectName.requestFocus();

                }
            }
        });
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
            protected void onBindViewHolder(final ProjectActivity.EmployeeViewHolder employeeViewHolder, final int position, @NonNull final EmployeeModel employee) {
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

                    }
                });
                //TODO SET LONGCLICKLISTENER TO REOVE FROM LIST AND CHAnGE COLOUR BACK
                employeeViewHolder.setDate(employee.getDate());
                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AssignedEmployess assignedEmployess = new AssignedEmployess(employeeViewHolder.getName(), employeeViewHolder.getKey());
                        employeeViewHolder.view.setBackgroundColor(Color.GRAY);
                        assignedEmployessList.put(employeeViewHolder.getKey(), assignedEmployess);
                        employeeProjects.put(employeeViewHolder.getKey(),employeeViewHolder.getName());

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
        String uName;
        String key;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }

        public void setDate(String date) {
            TextView empDate = view.findViewById(R.id.user_single_status);
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
            CircleImageView circleImageView = view.findViewById(R.id.user_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }

    }
}








