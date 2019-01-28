package com.eunan.tracey.etimefinalyearproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProject extends AppCompatActivity {
    private final String TAG = "Create Project";
    private EditText projectName;
    private EditText projectLocation;
    private EditText projectDescription;

    private Button btnAddProject;
    // Database
    DatabaseReference databaseProject;

    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        databaseProject = FirebaseDatabase.getInstance().getReference("projects");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        btnAddProject = findViewById(R.id.btnAddProject);

        projectName = findViewById(R.id.editTextLoginEmail);
        projectDescription = findViewById(R.id.editTextDescription);
        projectLocation = findViewById(R.id.editTexttLoginPaswordPassword);

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = projectName.getText().toString().trim();
                String location = projectLocation.getText().toString().trim();
                String description = projectDescription.getText().toString().trim();
                if (!validate(name)) {
                    Toast.makeText(CreateProject.this,"Name Cannot Be Empty",Toast.LENGTH_LONG).show();
                } else if (!validate(location)) {
                    Toast.makeText(CreateProject.this,"Location Cannot Be Empty",Toast.LENGTH_LONG).show();
                } else if (!validate(description)) {
                    Toast.makeText(CreateProject.this, "Description Cannot Be Empty", Toast.LENGTH_LONG).show();
                }else{
                    String id = databaseProject.push().getKey();
                    Project project = new Project(name,location,description);
                    databaseProject.child(userId).child(id).setValue(project);

                    Toast.makeText(CreateProject.this, "Project Added", Toast.LENGTH_SHORT).show();
                    projectName.setText("");
                    projectLocation.setText("");
                    projectDescription.setText("");

                }
            }
        });
    }

    // Check length of password
    public boolean validate(String data) {
        Log.d(TAG, "validate: starts " + data);
        if(!TextUtils.isEmpty(data)){
            return true;
        }
        return false;
    }

}
