package com.eunan.tracey.etimefinalyearproject.bdhandler;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.register.RegisterActivity;
import com.eunan.tracey.etimefinalyearproject.token.Token;
import com.eunan.tracey.etimefinalyearproject.user.UserModel;
import com.eunan.tracey.etimefinalyearproject.user.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class DBHandler {

    DatabaseReference ref1;
    DatabaseReference ref2;
    FirebaseAuth firebaseAuth;
    Context context;
    String token;

    public DBHandler(Context context, String ref1, String ref2) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.ref1 = FirebaseDatabase.getInstance().getReference().child(ref1);
        this.ref2 = FirebaseDatabase.getInstance().getReference().child(ref2);
        this.context = context;

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });
    }

    // Overloaded Constructer
    public DBHandler(Context context, DatabaseReference ref1) {
        this.context = context;
        this.ref1 = ref1;
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateToken();
                    openActivity();
                } else {
                    Toast.makeText(context, "Error, login failed", Toast.LENGTH_LONG).show();
                }
            }


        });


    }

    private void openActivity() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getUid();
        ref1.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String title = dataSnapshot.child("title").getValue().toString();
                    if (title.equals("Employee"))
                        context.startActivity(new Intent(context, EmployeeProfileActivity.class));
                    else
                        context.startActivity(new Intent(context, EmployerProfileActivity.class));
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateToken() {
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                // update the token of the device if the users logs in on another device
                String token = instanceIdResult.getToken();
                ref1.child(currentUserId).child("token").setValue(token);
                ref2.child(currentUserId).child("tokenId").setValue(token);
            }
        });
    }

    public void registerUser(final String displayName, final String email, String password, final String title) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = currentUser.getUid();
                    Token tokenModel = new Token(token, uid);
                    ref2.child(uid).setValue(tokenModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserModel userModel = new UserModel(displayName, "default", "default", "default", email, token, title);
                                ref1.child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (title.equals("Employee"))
                                                context.startActivity(new Intent(context, EmployeeProfileActivity.class));
                                            else
                                                context.startActivity(new Intent(context, EmployerProfileActivity.class));
                                            Toast.makeText(context, "Successfully Registered, ", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    // ((RegisterActivity)context).finish();
                } else {
                    Toast.makeText(context, "Error, could not create user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
