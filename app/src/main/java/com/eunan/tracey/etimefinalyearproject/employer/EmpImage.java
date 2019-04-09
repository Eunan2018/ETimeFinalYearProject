package com.eunan.tracey.etimefinalyearproject.employer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.ImageAdapter;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.upload.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpImage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private String key1;
    private String key2;

    private DatabaseReference imageRef;
    private List<Upload> uploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_image);
        key1 = getIntent().getStringExtra("key1");
        key2 = getIntent().getStringExtra("key2");
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(this);
        linearlayoutmanager.setReverseLayout(true);
        linearlayoutmanager.setStackFromEnd(true);

        recyclerView = findViewById(R.id.recycler_view_emp_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearlayoutmanager);


        uploadList = new ArrayList<>();

        imageRef = FirebaseDatabase.getInstance().getReference("Images").child(key1).child(key2);

        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }
                imageAdapter = new ImageAdapter(EmpImage.this,uploadList);
                recyclerView.setAdapter(imageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EmpImage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
