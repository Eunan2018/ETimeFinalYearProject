package com.eunan.tracey.etimefinalyearproject.project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {
    private final String TAG = "ProjectModel Activity";
   // private boolean isSpinnerInitial = true; //As global variable
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
    private ArrayAdapter employeeAdapter;
    ListView listView;
    final List<Map<String,String>> empList = new ArrayList<>();
    final List employeeList = new ArrayList<>();
    final List tempList = new ArrayList<>();
    Map<String,String> map = new HashMap<>();
    final List<Map<String,String>> empMap = new ArrayList<>();
    private String employee;
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: starts ");
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        employeeDatabase.child("Employer").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: starts : " + dataSnapshot);

                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    String key = db.getKey();
                    usersDatabase.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            // TODO USE ID AS WELL AS NAME, ID CAN THEN BE USED TO FILTER PROJECTS
                            // TODO WILL HAVE TO USE MAP
                            String empUserName = dataSnapshot.child("name").getValue(String.class);
                            String empUserId = dataSnapshot.getKey();

                            map.put(empUserId,empUserName);
                            empList.add(map);
                            for(String value : map.values()){
                                tempList.add(value);
                           }
                            populateSpinner(tempList);
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.activity_project);


        // Firebase
        databaseProject = FirebaseDatabase.getInstance().getReference("Projects");
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
                    Toast.makeText(ProjectActivity.this, "ProjectModel Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(location)) {
                    Toast.makeText(ProjectActivity.this, "Location Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else if (!validate(description)) {
                    Toast.makeText(ProjectActivity.this, "Description Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else {
                    // Create unique key for each project
                    String id = databaseProject.push().getKey();
                    ProjectModel project = new ProjectModel(name, location, description,empMap);
                    databaseProject.child(userId).child(id).setValue(project);

                    Toast.makeText(ProjectActivity.this, "ProjectModel Added", Toast.LENGTH_SHORT).show();
                    // clear all and reset focus
                    projectName.setText("");
                    projectLocation.setText("");
                    projectDescription.setText("");
                    employeeList.clear();
                    empMap.clear();
                    employeeAdapter.notifyDataSetChanged();
                    projectName.requestFocus();
                }
            }
        });
    }


    public void populateSpinner(final List employeeNames) {
        // load the employee names into the adapter and load the spinner
        ArrayAdapter<String> employees = new ArrayAdapter<String>(ProjectActivity.this, android.R.layout.simple_spinner_item, employeeNames);
        employees.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empSpinner.setAdapter(employees);

        // fires when user clicks on employee
        empSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                // TODO FIX FIRST ELEMENT IN SPINNER NOT TO SHOW
//                if (isSpinnerInitial) {
//                    isSpinnerInitial = false;
//                    return;
//                }

                // get the selected user and load into list
                employee = parent.getSelectedItem().toString();
                employeeList.add(employee);

                // loop through the map and match the value to the key
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    if (entry.getValue().equals(employee))
                    {
                        // store the key from entry to the list
                        Map<String,String> tempMap = new HashMap<>();
                        tempMap.put(entry.getKey(),employee);
                        empMap.add(tempMap);
                    }
                }
                //load the selected employee into the list
                employeeAdapter = new ArrayAdapter<>(ProjectActivity.this, android.R.layout.simple_list_item_1, employeeList);
                listView.setAdapter(employeeAdapter);
                Log.d(TAG, "onItemSelected: spinner" + employee);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                employeeList.remove(employeeList.get(position));
                empMap.remove(position);
                listView.requestFocus();
                employeeAdapter.notifyDataSetChanged();

                return false;
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
