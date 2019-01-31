package com.eunan.tracey.etimefinalyearproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {
    private final String TAG = "Project Activity";

    // Layout
    private EditText projectName;
    private EditText projectLocation;
    private EditText projectDescription;
    private Button addProject;

    // Database
    DatabaseReference databaseProject;
    DatabaseReference usersDatabase;
    DatabaseReference employeeDatabase;
    FirebaseUser currentUser;
    Spinner empSpinner;
    private ArrayAdapter adapter;
    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_project);

        // Firebase
        databaseProject = FirebaseDatabase.getInstance().getReference("projects");
        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        employeeDatabase = FirebaseDatabase.getInstance().getReference();

        // Layout
        addProject = findViewById(R.id.button_add_project);
        projectName = findViewById(R.id.edit_text_project_name);
        projectDescription = findViewById(R.id.edit_text_project_description);
        projectLocation = findViewById(R.id.edit_text_project_location);
        empSpinner = findViewById(R.id.spinner_employes);
        listView = findViewById(R.id.list_view_names);
        // Onclick
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = projectName.getText().toString().trim();
                String location = projectLocation.getText().toString().trim();
                String description = projectDescription.getText().toString().trim();
                if (!validate(name)) {
                    Toast.makeText(ProjectActivity.this, "Name Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(location)) {
                    Toast.makeText(ProjectActivity.this, "Location Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(description)) {
                    Toast.makeText(ProjectActivity.this, "Description Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else {
                    // Create unique key for each project
                    String id = databaseProject.push().getKey();
                    Project project = new Project(name, location, description);
                    databaseProject.child(userId).child(id).setValue(project);

                    Toast.makeText(ProjectActivity.this, "Project Added", Toast.LENGTH_SHORT).show();
                    projectName.setText("");
                    projectLocation.setText("");
                    projectDescription.setText("");

                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: starts ");
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        employeeDatabase.child("Friends").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: starts : " + dataSnapshot);
                final List<String> empList = new ArrayList<>();
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    String key = db.getKey();
                    usersDatabase.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String empUserName = dataSnapshot.child("name").getValue(String.class);
                            empList.add(empUserName);
                            populateSpinner(empList);
                            Log.d(TAG, "onDataChange: starts " + empUserName);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            String error = databaseError.toString();
                            Toast.makeText(ProjectActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String error = databaseError.toString();
                Toast.makeText(ProjectActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void populateSpinner(final List names) {

        ArrayAdapter<String> employees = new ArrayAdapter<>(ProjectActivity.this, android.R.layout.simple_spinner_item, names);
        employees.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empSpinner.setAdapter(employees);
        final List x = new ArrayList();
        Log.d(TAG, "onDataChange: below spinner " + employees.toString());
        empSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                // TODO FIX FIRST ELEMENT IN SPINNER NOT TO SHOW
                String n = empSpinner.getSelectedItem().toString();
                x.add(n);
                adapter = new ArrayAdapter(ProjectActivity.this, android.R.layout.simple_list_item_1, x);
                //adapter.add(n);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(ProjectActivity.this, n, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

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

}
