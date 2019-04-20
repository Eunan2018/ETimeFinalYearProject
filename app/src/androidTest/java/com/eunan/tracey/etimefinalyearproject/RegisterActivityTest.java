package com.eunan.tracey.etimefinalyearproject;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.eunan.tracey.etimefinalyearproject.bdhandler.DBHandler;
import com.eunan.tracey.etimefinalyearproject.register.RegisterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mRegisterActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);


    // Create RegisterActivity reference
    private RegisterActivity mRegisterActivity = null;

    // Initialise RegisterActivity
    @Before
    public void setUp() {
        mRegisterActivity = mRegisterActivityTestRule.getActivity();
    }

    // If the button is found the Activity exists
    @Test
    public void testLaunch(){
        View view =  mRegisterActivity.findViewById(R.id.button_sign_up);
        assertNotNull(view);
    }

    // Stop Activity
    @After
    public void tearDown() {
        mRegisterActivity = null;
    }


}