package com.eunan.tracey.etimefinalyearproject;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static FirebaseAuth auth;

    @ClassRule
    public static ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void register() {
        FirebaseApp.initializeApp(rule.getActivity());
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword("john@hotmail.com", "wexr567123")
                .addOnCompleteListener(rule.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            Assert.assertTrue(task.isSuccessful());
                    }
                });
    }

   @Test
    public void login() throws Exception{
        Thread.sleep(2000);
        FirebaseApp.initializeApp(rule.getActivity());
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("john@hotmail.com", "wexr567123")
                .addOnCompleteListener(rule.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Assert.assertTrue(task.isSuccessful());//
                    }
                });
    }
}


