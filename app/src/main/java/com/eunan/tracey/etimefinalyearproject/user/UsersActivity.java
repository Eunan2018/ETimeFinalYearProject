package com.eunan.tracey.etimefinalyearproject.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.eunan.tracey.etimefinalyearproject.profile.ProfileActivity;
import com.eunan.tracey.etimefinalyearproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jxl.biff.FilterModeRecord;

public class UsersActivity extends AppCompatActivity {

    // UI
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    // Firebase
    private DatabaseReference userRef;
    private FirebaseUser firebaseUser;

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        currentUser =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        // UI
        toolbar = findViewById(R.id.users_app_bar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(d);
        recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = String.valueOf(dataSnapshot.child("title").getValue());
                if(title.equals("Employee"))
                    startActivity(new Intent(UsersActivity.this, EmployeeProfileActivity.class));
                else
                    startActivity(new Intent(UsersActivity.this, EmployerProfileActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
       final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                        .setQuery(userRef, UserModel.class)
                        .build();

       FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<UserModel, UsersViewHolder>(options) {

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout, parent, false);
                return new UsersViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int position, @NonNull UserModel users) {
                final String userId = getRef(position).getKey();

                    usersViewHolder.setName(users.getName());
                    usersViewHolder.setStatus(users.getStatus());
                    usersViewHolder.setImage(UsersActivity.this, users.getThumbImage());

                    usersViewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                            profileIntent.putExtra("id",userId);
                            startActivity(profileIntent);
                        }
                    });


            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView userName = view.findViewById(R.id.user_single_name);
            userName.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatus = view.findViewById(R.id.user_single_status);
            userStatus.setText(status);
        }

        public void setImage(Context context, String user_image){
            CircleImageView circleImageView = view.findViewById(R.id.history_image);
            Picasso.with(context).load(user_image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }
    }
}