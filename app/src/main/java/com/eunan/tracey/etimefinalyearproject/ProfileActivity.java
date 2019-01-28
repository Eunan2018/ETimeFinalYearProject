package com.eunan.tracey.etimefinalyearproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    // Layout
    private TextView txtProfileName, txtProfileStatus, txtProfileFriendsCount;
    private Button btnProfileSendRequest;
    private ImageView imgProfileImage;

    private String currentState;

    // Firebase
    DatabaseReference userDatabase;
    DatabaseReference friendRequestDatabase;
    DatabaseReference friendsDatabase;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Layout
        txtProfileName = findViewById(R.id.textViewProfileDisplayName);
        txtProfileFriendsCount = findViewById(R.id.textViewProfileFriends);
        txtProfileStatus = findViewById(R.id.textViewProfileStatus);
        imgProfileImage = findViewById(R.id.imageViewProfile);
        btnProfileSendRequest = findViewById(R.id.buttonProfileFirendRequest);

        currentState = "notFriends";


        // Firebase
        final String userId = getIntent().getStringExtra("userId");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                txtProfileName.setText(displayName);
                txtProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(imgProfileImage);

                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userId)){
                            String requestType = dataSnapshot.child(userId).child("requestType").getValue().toString();
                            if(requestType.equals("received")){
                                btnProfileSendRequest.setEnabled(true);
                                currentState = "requestRecieved";
                                btnProfileSendRequest.setText("Accept Friend Request");
                            }else if(requestType.equals("sent")){
                                currentState = "requestSent";
                                btnProfileSendRequest.setText("Cancel");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnProfileSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnProfileSendRequest.setEnabled(false);

                //----------not friends ----------------------------------
                if (currentState.equals("notFriends")) {

                    friendRequestDatabase.child(currentUser.getUid()).child(userId).child("requestType").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        friendRequestDatabase.child(userId).child(currentUser.getUid()).child("requestType")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                btnProfileSendRequest.setEnabled(true);
                                                currentState = "requestSent";
                                                btnProfileSendRequest.setText("Cancel Friend Request");
                                                Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed Request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                //--------------------- cancel request ---------------------------

                if(currentState.equals("requestSent")){
                    friendRequestDatabase.child(currentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDatabase.child(userId).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnProfileSendRequest.setEnabled(true);
                                    currentState = "notFriends";
                                    btnProfileSendRequest.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }

                //------------request received -------------
                if(currentState.equals("requestReceived")){

                    final String date = DateFormat.getDateInstance().format(new Date());
                    friendsDatabase.child(currentUser.getUid()).child(userId).setValue(date)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendsDatabase.child(userId).child(currentUser.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendRequestDatabase.child(currentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendRequestDatabase.child(userId).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            btnProfileSendRequest.setEnabled(true);
                                                            currentState = "Friend";
                                                            btnProfileSendRequest.setText("UnFriend");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
}
