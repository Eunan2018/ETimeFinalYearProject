package com.eunan.tracey.etimefinalyearproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class EmployeeFragment extends android.support.v4.app.Fragment {

    private final String TAG = "EmployeeFragment";
    // Firebase
    private RecyclerView recyclerView;
    private DatabaseReference employeeDatabase;
    private DatabaseReference usersDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;


    // Layout
    private FloatingActionButton fab;
    private View view;

    public EmployeeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_employee, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_employees);

        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        employeeDatabase = FirebaseDatabase.getInstance().getReference().child("Employer").child(currentUserId);
        employeeDatabase.keepSynced(true);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabaseReference.keepSynced(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: starts");
        FirebaseRecyclerOptions<Employee> options =
                new FirebaseRecyclerOptions.Builder<Employee>()
                        .setQuery(employeeDatabase, Employee.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Employee, EmployeeViewHolder>(options) {

            @Override
            public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new EmployeeViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final EmployeeViewHolder employeeViewHolder, final int position, @NonNull Employee employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                final String userId = getRef(position).getKey();

                usersDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumbImage").getValue().toString();
                        employeeViewHolder.setName(name);
                        employeeViewHolder.setImage(getContext(), image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                employeeViewHolder.setDate(employee.getDate());

                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: starts");
                        // Build a dialog box where the user can choose either profile or time-sheet
                        CharSequence options[] = new CharSequence[]{"Open Profile", "View Time Sheet"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                        switch (position) {
                            case 0:
                                Intent profileIntent = new Intent(getContext().getApplicationContext(), ProfileActivity.class);
                                profileIntent.putExtra("from_user_id", userId);

                                startActivity(profileIntent);
                                break;
                            case 1:
                                Intent timeSheetIntent = new Intent(getContext().getApplicationContext(), EmployerTimeSheetActivity.class);
                                timeSheetIntent.putExtra("ts_id", userId);
                                timeSheetIntent.putExtra("name", employeeViewHolder.getName());
                                timeSheetIntent.putExtra("employer_id",currentUserId);
                                startActivity(timeSheetIntent);
                                break;
                            default :
                                break;
                        }
                            }
                        });
                        builder.show();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: starts");
        fab = getView().findViewById(R.id.fab_add_employee);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UsersActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}