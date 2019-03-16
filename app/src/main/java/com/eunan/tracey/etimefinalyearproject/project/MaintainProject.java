package com.eunan.tracey.etimefinalyearproject.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.AssignedEmployess;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.Employee;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeFragment;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerTimeSheetActivity;
import com.eunan.tracey.etimefinalyearproject.profile.ProfileActivity;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MaintainProject extends AppCompatActivity {

    private final static String TAG = "MaintainProject";


    interface CallBack {
        void onCallBack(String pushId);
    }

    private TextView txtProjectName;
    private TextView txtProjectLocation;
    private TextView txtProjectDescrition;
    private Button btnAddEmployee;
    private String names;
    private String location;
    private String descrition;
    private int timestamp;

    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    // Firebase
    private RecyclerView recycler_view_mp;
    private DatabaseReference employeeRef;
    private DatabaseReference usersRef;
    private DatabaseReference projectRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_project);

        descrition = getIntent().getStringExtra("description");
        location = getIntent().getStringExtra("location");
        names = getIntent().getStringExtra("title");
        timestamp = getIntent().getIntExtra("timestamp", 0);

        txtProjectDescrition = findViewById(R.id.text_view_project_des_mp);
        txtProjectLocation = findViewById(R.id.text_view_proj_loc_mp);
        txtProjectName = findViewById(R.id.text_view_project_name_mp);
        btnAddEmployee = findViewById(R.id.button_add_emplyee_mp);
        recycler_view_mp = findViewById(R.id.recycler_view_mp);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        txtProjectDescrition.setText(descrition);
        txtProjectLocation.setText(location);
        txtProjectName.setText(names);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");
        employeeRef = FirebaseDatabase.getInstance().getReference().child("Employer").child(currentUserId);
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
                    String time = childSnapShot.child("projectTimestamp").getValue().toString();
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
                    protected void onBindViewHolder(final EmployeeViewHolder employeeViewHolder, final int position, @NonNull EmployeeModel employee) {
                        Log.d(TAG, "onBindViewHolder: starts");
                        final String userId = getRef(position).getKey();

                        Log.d(TAG, "onBindViewHolder: key " + userId);
                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String image = dataSnapshot.child("thumbImage").getValue().toString();
                                employeeViewHolder.setName(name);
                                employeeViewHolder.setImage(MaintainProject.this, image);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        employeeViewHolder.setDate(employee.getDate());

                        employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                projectRef.child(currentUserId).child(pushId).child("assignedEmployessList").child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MaintainProject.this, "Deleted" + userId, Toast.LENGTH_SHORT).show();
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
            CircleImageView circleImageView = view.findViewById(R.id.user_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }

    }
}

