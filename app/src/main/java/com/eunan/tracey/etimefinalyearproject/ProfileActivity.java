package com.eunan.tracey.etimefinalyearproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName;
    private Button send, decline;

    private DatabaseReference userDatabase;

    private ProgressDialog progressDialog;

    private DatabaseReference friendReqDatabase;
    private DatabaseReference friendDatabase;
    private DatabaseReference notificationDatabase;

    private DatabaseReference rootRef;

    private FirebaseUser currentUser;

    private Status currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("from_user_id");

        rootRef = FirebaseDatabase.getInstance().getReference();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
       // notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = (ImageView) findViewById(R.id.image_view_profile);
        profileName = (TextView) findViewById(R.id.text_view_profile_display_name);
        send = (Button) findViewById(R.id.button_profile_friend_request);
        decline = (Button) findViewById(R.id.button_profile_decline_request);


        currentState = Status.NOT_EMPLOYED;

        decline.setVisibility(View.INVISIBLE);
        decline.setEnabled(false);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                profileName.setText(display_name);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(profileImage);

                if(currentUser.getUid().equals(user_id)){

                    decline.setEnabled(false);
                    decline.setVisibility(View.INVISIBLE);

                    send.setEnabled(false);
                    send.setVisibility(View.INVISIBLE);

                }


                //--------------- EMPLOYEE LIST / REQUEST FEATURE -----

                friendReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                currentState = Status.RECEIVED;
                                send.setText("Accept Project Request");

                                decline.setVisibility(View.VISIBLE);
                                decline.setEnabled(true);


                            } else if(req_type.equals("sent")) {

                                currentState = Status.SENT;
                                send.setText("Cancel Project Request");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);
                            }
                            progressDialog.dismiss();

                        } else {

                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        currentState = Status.EMPLOYED;
                                        send.setText("Remove From Project");

                                        decline.setVisibility(View.INVISIBLE);
                                        decline.setEnabled(false);

                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    progressDialog.dismiss();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send.setEnabled(false);

                // --------------- NOT EMPLOYED STATE ------------

                if(currentState.equals(Status.NOT_EMPLOYED)){

                    DatabaseReference newNotificationref = rootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", currentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestProjectMap = new HashMap();
                    requestProjectMap.put("Friend_req/" + currentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestProjectMap.put("Friend_req/" + user_id + "/" + currentUser.getUid() + "/request_type", "received");
                    requestProjectMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    rootRef.updateChildren(requestProjectMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {
                                currentState = Status.RECEIVED;
                                send.setText("Remove From Project");
                            }

                            send.setEnabled(true);

                        }
                    });

                }


                // - -------------- CANCEL REQUEST STATE ------------

                if(currentState.equals(Status.SENT)){

                    friendReqDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendReqDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    send.setEnabled(true);
                                    currentState = Status.NOT_EMPLOYED;
                                    send.setText("Send Project Request");

                                    decline.setVisibility(View.INVISIBLE);
                                    decline.setEnabled(false);


                                }
                            });

                        }
                    });

                }


                // ------------ REQ RECEIVED STATE ----------

                if(currentState.equals(Status.RECEIVED)){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map projectMap = new HashMap();
                    projectMap.put("Friends/" + user_id + "/"  + currentUser.getUid() + "/date", currentDate);


                    projectMap.put("Friend_req/" + currentUser.getUid() + "/" + user_id, null);
                    projectMap.put("Friend_req/" + user_id + "/" + currentUser.getUid(), null);


                    rootRef.updateChildren(projectMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                send.setEnabled(true);
                                currentState = Status.EMPLOYED;
                                send.setText("Remove From Project");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }


                // ------------ UNFRIENDS ---------

                if(currentState.equals(Status.EMPLOYED)){

                    Map removeFromProject = new HashMap();
                    removeFromProject.put("Friends/" + currentUser.getUid() + "/" + user_id, null);
                    removeFromProject.put("Friends/" + user_id + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(removeFromProject, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {



                            if(databaseError == null){

                                currentState = Status.NOT_EMPLOYED;
                                send.setText("Send Project Request");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }

                            send.setEnabled(true);

                        }
                    });

                }
            }
        });


    }

}