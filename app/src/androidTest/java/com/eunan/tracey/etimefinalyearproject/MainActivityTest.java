package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    FirebaseAuth auth;
    DatabaseReference userRef;
    FirebaseDatabase firebaseDatabase;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void login(){
        FirebaseApp.initializeApp(rule.getActivity());
        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword("bono@hotmail.com", "123456")
                .addOnCompleteListener(rule.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login activity", "signInWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    @Test
    public void loginTesting() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.edit_text_login_email)).perform(typeText("bono@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_login_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void recyclerViewTest() throws InterruptedException{
        login();
        Thread.sleep(4000);
       // onView(withId(R.id.recycler_view_users)).perform(RecyclerViewActions.scrollToPosition(3));
        //onView(withId(R.id.entertainmentSpotRecyclerView)).perform(RecyclerViewActions.scrollToHolder(...));
    }

    @Test
    @MediumTest
    public void signOutTest() throws InterruptedException {
        Thread.sleep(6000);
        auth.signOut();
        Thread.sleep(2000);
        login();
        Thread.sleep(2000);
    }

    @Test
    @LargeTest
    public void checkProfileTest() throws InterruptedException{
        Thread.sleep(6000);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("Users");
        FirebaseApp.initializeApp(rule.getActivity());
        auth = FirebaseAuth.getInstance();

        Looper.prepare();
        final MainActivity obj = new MainActivity();

        if(auth.getCurrentUser() != null){

            final String user_id = auth.getCurrentUser().getUid();
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user_id)){
                        obj.startActivity(new Intent(obj.getApplicationContext(), ProfileActivity.class));
                        obj.finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        Thread.sleep(4000);
    }
}