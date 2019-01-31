package com.eunan.tracey.etimefinalyearproject;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends android.support.v4.app.Fragment {

    private final String TAG = "ProjectFragment";
    private FloatingActionButton fab;
    RecyclerView recyclerView;
    List<Project> projectList;
    View view;
    DatabaseReference databaseProject;
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
        databaseProject = FirebaseDatabase.getInstance().getReference("projects");

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
        databaseProject.child(id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: starts " + dataSnapshot.toString());
                projectList.clear();
                for(DataSnapshot projectSnapshot : dataSnapshot.getChildren()){
                    Project project = projectSnapshot.getValue(Project.class);
                    projectList.add(project);
                }
                adapter = new RecyclerViewAdapter(getActivity(),projectList);
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fab.isShown())
                    fab.hide();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}
