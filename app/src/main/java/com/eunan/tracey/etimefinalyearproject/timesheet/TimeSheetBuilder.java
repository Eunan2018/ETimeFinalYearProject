package com.eunan.tracey.etimefinalyearproject.timesheet;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
import com.eunan.tracey.etimefinalyearproject.project.MaintainProject;
import com.eunan.tracey.etimefinalyearproject.project.ProjectActivity;
import com.eunan.tracey.etimefinalyearproject.project.ProjectModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class TimeSheetBuilder extends AppCompatActivity {

    private static final String TAG = "TimeSheetBuilder";

    private EditText hours;
    private EditText comment;
    private Button btnDone;
    private RecyclerView recyclerView;
    TimeSheetModel timesheet;

    interface CallBack {
        void onCallBack(String pushId);
    }

    public static HashMap<String, TimeSheetModel> timesheetMap = new HashMap();

    // Firebase
    private DatabaseReference timesheetRef;
    private DatabaseReference projectRef;
    private DatabaseReference employerRef;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private String userId;
    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_builder);

        hours = findViewById(R.id.edit_text_ts_hrs);
        comment = findViewById(R.id.edit_text_ts_comments);
        btnDone = findViewById(R.id.button_done_ts);
        recyclerView = findViewById(R.id.recycler_view_ts_builder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        timesheetRef = FirebaseDatabase.getInstance().getReference().child("TimeSheet");
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");
        employerRef = FirebaseDatabase.getInstance().getReference().child("Employer");


        employerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                  final  String employerKey = child.getKey();
                    Log.d(TAG, "onDataChange: employer key: " + employerKey);
                    for (DataSnapshot postChild : child.getChildren()) {
                        final String employeeKey = postChild.getKey();
                        if (TextUtils.equals(employeeKey, userId)) {
                            Log.d(TAG, "onDataChange: employee key: " + employeeKey + " userId: " + userId);
                            projectRef.child(employerKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        String pushId = ds.getKey();
                                        String projectName = ds.child("projectName").getValue(String.class);
                                        Log.d(TAG, "onDataChange: pushId: " + pushId);
                                        Log.d(TAG, "onDataChange: projectName: " + projectName);
                                        Map<String, AssignedEmployess> assignedEmployessMap = (Map<String, AssignedEmployess>) ds.child("assignedEmployessList").getValue();
                                        if (assignedEmployessMap.containsKey(employeeKey) && flag) {
                                            loadRecyclerView(employerKey,employeeKey,pushId);

                                        }
                                        Log.d(TAG, "onDataChange: map: " + assignedEmployessMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // EMPLOYER
            }
        });
    }


    private void loadRecyclerView(final String employerKey,final String employeeKey,final String pushId) {
        FirebaseRecyclerOptions<ProjectModel> options =
                new FirebaseRecyclerOptions.Builder<ProjectModel>()
                        .setQuery(projectRef.child(employerKey).child(pushId).child("assignedEmployessList").orderByChild("name"), ProjectModel.class)
                        .build();
        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ProjectModel, ProjectViewHolder>(options) {

            @Override
            public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.project_layout_employee, parent, false);
                return new ProjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ProjectViewHolder projectViewHolder, final int position, @NonNull ProjectModel project) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String userId = getRef(position).getKey();

                Log.d(TAG, "onBindViewHolder: key " + userId);
                projectRef.child(employerKey).child(pushId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String location = dataSnapshot.child("projectLocation").getValue().toString();
                        projectViewHolder.setLocation(location);
                        //  projectViewHolder.setImage(TimeSheetBuilder.this, image);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "ProjectViewHolder";
        View view;
        String projLoc;
        public TextView location;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }

        public void setLocation(String loc) {
            location = view.findViewById(R.id.textview_title);
            location.setText(loc);
            projLoc = loc;
        }

        public String getLocation() {
            return projLoc;
        }

        public void setImage(Context context, String image) {
            CircleImageView circleImageView = view.findViewById(R.id.user_image);
            Picasso.with(context).load(image).placeholder(R.drawable.home).into(circleImageView);
        }

    }
}



