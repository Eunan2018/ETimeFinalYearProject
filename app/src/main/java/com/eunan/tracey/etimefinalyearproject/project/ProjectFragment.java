package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;
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

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectFragment extends android.support.v4.app.Fragment {

    private final String TAG = "ProjectFragment";
    private FloatingActionButton fab;
    RecyclerView recyclerView;
    List<ProjectModel> projectList;
    View view;
    DatabaseReference projectRef;
    FirebaseUser currentUser;
    RecyclerViewAdapter adapter;
    String id;

    public ProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();
        projectRef = FirebaseDatabase.getInstance().getReference("Projects");

        projectList = new ArrayList<>();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_project, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_project);

        return view;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: starts");
        super.onStart();
        projectRef.child(id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: starts " + dataSnapshot.toString());
                projectList.clear();
                for(DataSnapshot projectSnapshot : dataSnapshot.getChildren()){
                    ProjectModel project = projectSnapshot.getValue(ProjectModel.class);
                    projectList.add(project);
                }
                adapter = new RecyclerViewAdapter(getActivity(), projectList);
                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                Log.d(TAG, "onDataChange: ends");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onStart: ends");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab = getView().findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProjectActivity.class);
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

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String uName;
        String key;

        public ProjectViewHolder(View itemView) {
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
            CircleImageView circleImageView = view.findViewById(R.id.history_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }

    }
}
