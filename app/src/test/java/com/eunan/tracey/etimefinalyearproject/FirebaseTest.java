//package com.eunan.tracey.etimefinalyearproject;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.eunan.tracey.etimefinalyearproject.employee.EmployeeModel;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//public class FirebaseTest extends Application {
//
//    private CountDownLatch authSignal = null;
//    private FirebaseAuth auth;
//    Context context = new Activity();
//    @Before
//    public void setUp() throws InterruptedException {
//        FirebaseApp.initializeApp(context);
//        authSignal = new CountDownLatch(1);
//        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() == null) {
//            auth.signInWithEmailAndPassword("cara@hotmail.com", "123456").addOnCompleteListener(
//                    new OnCompleteListener<AuthResult>() {
//
//                        @Override
//                        public void onComplete(@NonNull final Task<AuthResult> task) {
//
//                            final AuthResult result = task.getResult();
//                            final FirebaseUser user = result.getUser();
//                            authSignal.countDown();
//                        }
//                    });
//        } else {
//            authSignal.countDown();
//        }
//        authSignal.await(10, TimeUnit.SECONDS);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        if (auth != null) {
//            auth.signOut();
//            auth = null;
//        }
//    }
//
//    @Test
//    public void testWrite() throws InterruptedException {
//        final CountDownLatch writeSignal = new CountDownLatch(1);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Employer");
//        String x = "X";
//        String y = "Y";
//        EmployeeModel employee = new EmployeeModel();
//        employee.setDate("xx");
//        employee.setName("xx");
//        myRef.setValue(employee)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                    @Override
//                    public void onComplete(@NonNull final Task<Void> task) {
//                        writeSignal.countDown();
//                    }
//                });
//
//        writeSignal.await(10, TimeUnit.SECONDS);
//    }
//}
//
//
