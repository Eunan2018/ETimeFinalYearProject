package com.eunan.tracey.etimefinalyearproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProject extends AppCompatActivity {
    private EditText projectName;
    private EditText projectLocation;
    private EditText projectDescription;

    private Button btnAddProject;
    // Database
    DatabaseReference databaseProject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        databaseProject = FirebaseDatabase.getInstance().getReference("projects");

        btnAddProject = findViewById(R.id.btnAddProject);

        projectName = findViewById(R.id.editTextProjectName);
        projectDescription = findViewById(R.id.editTextDescription);
        projectLocation = findViewById(R.id.editTextLocation);

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = projectName.getText().toString().trim();
                String location = projectLocation.getText().toString().trim();
                String description = projectDescription.getText().toString().trim();

                //TODO verify all
                if(!TextUtils.isEmpty(name)){
                    // Creates a unique id for each project
                    String id = databaseProject.push().getKey();
                    Project project = new Project(id,name,location,description);
                    databaseProject.child(id).setValue(project);

                    Toast.makeText(CreateProject.this, "Project Added", Toast.LENGTH_SHORT).show();
                    projectName.setText("");
                    projectLocation.setText("");
                    projectDescription.setText("");
                }else{
                    Toast.makeText(CreateProject.this,"Name Cannot Be Empty",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
