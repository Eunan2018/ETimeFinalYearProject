package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.AssignedEmployess;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AddEmployeeActivity extends AppCompatActivity {
    private final String TAG = "AddEmployeeActivity";
    // Layout

    private Button addEmployee;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView_mp;

    // Database
    private DatabaseReference projectRef;
    private DatabaseReference userRef;
    private DatabaseReference employeeRef;
    private FirebaseUser currentUser;

    // Variables
    private String currentUserId;
    private int timestamp;
    private HashMap<String, Object> assignedEmployessList;
    AssignedEmployess assignedEmployess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        assignedEmployessList = new HashMap();
        assignedEmployess = new AssignedEmployess();

        // Firebase
        projectRef = FirebaseDatabase.getInstance().getReference("Projects");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        employeeRef = FirebaseDatabase.getInstance().getReference().child("Employer");
        addEmployee = findViewById(R.id.button_update_add_employee);
        recyclerView = findViewById(R.id.recycler_view_add_employee);
        recyclerView_mp = findViewById(R.id.recycler_view_mp);
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
                        .setQuery(employeeRef.child(currentUserId), EmployeeModel.class)
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

                    }

                });

                addEmployee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        projectRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String key = ds.getKey();
                                    String time = ds.child("projectTimestamp").getValue().toString();
                                    if (time.equals(String.valueOf(timestamp))) {
                                        projectRef.child(currentUserId).child(key).child("assignedEmployessList").updateChildren(assignedEmployessList)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
