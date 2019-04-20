package com.eunan.tracey.etimefinalyearproject.bdhandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.Title;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.eunan.tracey.etimefinalyearproject.token.Token;
import com.eunan.tracey.etimefinalyearproject.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private final String TAG = "DBHandler";
    private DatabaseReference ref1;
    private DatabaseReference ref2;
    private FirebaseAuth auth;
    private Context context;
    private String token;

    private ProgressDialog progressDialog;

    public DBHandler(Context context, String ref1, String ref2) {
        auth = FirebaseAuth.getInstance();
        this.ref1 = FirebaseDatabase.getInstance().getReference().child(ref1);
        this.ref2 = FirebaseDatabase.getInstance().getReference().child(ref2);
        this.context = context;

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // Overloaded Constructer
    public DBHandler(Context context, DatabaseReference ref1) {
        this.context = context;
        this.ref1 = ref1;
    }

    public DBHandler(String ref1) {
        this.ref1 = FirebaseDatabase.getInstance().getReference().child(ref1);
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    }, 1000);
                    updateToken();
                    checkUserCredentials();
                } else {
                    progressDialog.cancel();
                    Toast.makeText(context, "Error, login failed", Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    private void checkUserCredentials() {
        final String uid = getUserId();
        ref1.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue().toString();
                openActivity(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + String.valueOf(databaseError));
            }
        });
    }

    private void openActivity(String title) {
        if (Title.EMPLOYEE.getTitle().equals(title))
            context.startActivity(new Intent(context, EmployeeProfileActivity.class));
        else
            context.startActivity(new Intent(context, EmployerProfileActivity.class));
    }

    private void updateToken() {
        final String currentUserId = getUserId();
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    }, 1000);
                    createTokenObject(displayName, email, title);
                } else {
                    progressDialog.cancel();
                    Toast.makeText(context, "Error, could not create user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createTokenObject(final String displayName, final String email, final String title) {
      final String uid = getUserId();
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
                                openActivity(title);
                                Toast.makeText(context, "Successfully Registered, ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private String getUserId(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

}
