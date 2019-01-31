package com.eunan.tracey.etimefinalyearproject;

import android.view.View;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    // Create test rule of MainActivity
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    // Create MainActivity reference
    private MainActivity mMainActivity = null;

    @Before
    public void setUp(){
        mMainActivity = mMainActivityTestRule.getActivity();
    }

    // If the button is found the Activity exists
    @Test
    public void testLaunch(){
        View view =  mMainActivity.findViewById(R.id.button_login);
        assertNotNull(view);
    }

    @Test
    public void testValidatePassword() {
        String invalidPassword = "12345";
        String validPassword = "123456";
        assertFalse(mMainActivity.validatePassword(invalidPassword));
        assertTrue(mMainActivity.validatePassword(validPassword));
    }

    @Test
    public void testValidateEmail() {
        String validEmail = "joebloggs@hotmail.com";
        String invalidEmail = "joebloggsemail.co";
        assertTrue(mMainActivity.validateEmail(validEmail));
        assertFalse(mMainActivity.validateEmail(invalidEmail));
    }

    // Stop Activity
    @After
    public void tearDown() {
        mMainActivity = null;
    }
}