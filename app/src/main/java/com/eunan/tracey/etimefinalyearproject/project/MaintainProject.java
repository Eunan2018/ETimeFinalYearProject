package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MaintainProject extends AppCompatActivity {

    private final static String TAG = "MaintainProject";

    interface CallBack {
        void onCallBack(String pushId);
    }

    private int timestamp;
    // Layout
    private Toolbar toolbar;
    private String currentUserId;
    // Firebase
    private RecyclerView recycler_view_mp;
    private DatabaseReference employeeRef;
    private DatabaseReference usersRef;
    private DatabaseReference projectRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_project);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Set the action bar with name and logo
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String location = getIntent().getStringExtra("location");
        String names = getIntent().getStringExtra("title");
        timestamp = getIntent().getIntExtra("timestamp", 0);

        TextView txtProjectLocation = findViewById(R.id.text_view_proj_loc_mp);
        TextView txtProjectName = findViewById(R.id.text_view_project_name_mp);
        Button btnAddEmployee = findViewById(R.id.button_add_emplyee_mp);
        recycler_view_mp = findViewById(R.id.recycler_view_mp);
        FirebaseAuth firebaseAuth;
        txtProjectLocation.setText(location);
        txtProjectName.setText(names);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");
        DatabaseReference employerRef = FirebaseDatabase.getInstance().getReference().child("Employer").child(currentUserId);
        employerRef.keepSynced(true);
        employeeRef = FirebaseDatabase.getInstance().getReference().child("EmployeeProjects");
        employeeRef.keepSynced(true);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.keepSynced(true);


        recycler_view_mp.setHasFixedSize(true);
        recycler_view_mp.setLayoutManager(new LinearLayoutManager(MaintainProject.this));

        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintainProject.this, AddEmployeeActivity.class);
                intent.putExtra("time",timestamp);
                startActivity(intent);
            }
        });
    }

    private void getPushId(final CallBack callBack) {

        projectRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    String key = childSnapShot.getKey();
                    String time = String.valueOf(childSnapShot.child("projectTimestamp").getValue());
                    Log.d(TAG, "onDataChange: time" + time);
                    Log.d(TAG, "onDataChange: pushId" + key);
                    if (key != null && String.valueOf(timestamp).equals(time)) {
                        callBack.onCallBack(key);
                    } else {
                        Log.d(TAG, "onDataChange: " + key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.toString();
                Log.d(TAG, "onCancelled: " + error);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getPushId(new CallBack() {
            @Override
            public void onCallBack(final String pushId) {
                Query query = FirebaseDatabase.getInstance().getReference().child("Projects")
                        .child(currentUserId).child(pushId).child("assignedEmployessList").orderByChild("name");
                FirebaseRecyclerOptions<EmployeeModel> options =
                        new FirebaseRecyclerOptions.Builder<EmployeeModel>()
                                .setQuery(query, EmployeeModel.class)
                                .build();

                final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<EmployeeModel, EmployeeViewHolder>(options) {

                    @Override
                    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        Log.d(TAG, "onCreateViewHolder: starts");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.users_layout, parent, false);
                        return new EmployeeViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final EmployeeViewHolder employeeViewHolder, final int position, @NonNull EmployeeModel employee) {
                        Log.d(TAG, "onBindViewHolder: starts");
                        final String userId = getRef(position).getKey();
                        if(userId != null) {
                            Log.d(TAG, "onBindViewHolder: key " + userId);
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                                    String image = String.valueOf( dataSnapshot.child("thumbImage").getValue());                                    employeeViewHolder.setName(name);
                                    employeeViewHolder.setImage(MaintainProject.this, image);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    String error = databaseError.toString();
                                    Log.d(TAG, "onCancelled: " + error);
                                }
                            });
                        }
                        else{
                            Toast.makeText(MaintainProject.this, "Unable to retrieve employee", Toast.LENGTH_SHORT).show();
                        }
                        employeeViewHolder.setDate(employee.getDate());

                        employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                projectRef.child(currentUserId).child(pushId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       final String project = String.valueOf( dataSnapshot.child("projectName").getValue());
                                        removeEmployeeFromProject(pushId,"assignedEmployessList",userId,project);
                                        Map<String,AssignedEmployess> assignedEmployessList = (Map<String,AssignedEmployess>) dataSnapshot.child("assignedEmployessList")
                                                .getValue();
                                        if(assignedEmployessList.size() <= 1){
                                            projectRef.child(currentUserId).child(pushId).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(MaintainProject.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return false;
                            }
                        });
                    }
                };
                adapter.startListening();
                recycler_view_mp.setAdapter(adapter);

            }
        });


    }

    public boolean removeEmployeeFromProject(final String pushId,String employee,final String userId,final String projectName){
        projectRef.child(currentUserId).child(pushId).child(employee).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                employeeRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                            String project = String.valueOf( childSnapshot.child("project").getValue());
                            if(project.equals(projectName)){
                                    String key = childSnapshot.getKey();
                                    if(key != null) {
                                    employeeRef.child(userId).child(key).setValue(null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MaintainProject.this,String.valueOf(databaseError),Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(MaintainProject.this, "Deleted " + projectName, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MaintainProject.this, "Unable to delete employee " +  e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }


    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String uName;

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

        public String getName() {
            return uName;
        }

        public void setImage(Context context, String image) {
            CircleImageView circleImageView = view.findViewById(R.id.history_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }

    }
}

