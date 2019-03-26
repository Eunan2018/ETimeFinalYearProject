package com.eunan.tracey.etimefinalyearproject;

import android.view.View;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.main.MainActivity;

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
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    // Create MainActivity reference
    private MainActivity mainActivity = null;

    @Before
    public void setUp(){
        mainActivity = mainActivityTestRule.getActivity();
    }

    // If the button is found the Activity exists
    @Test
    public void testLaunch(){
        View view =  mainActivity.findViewById(R.id.button_login);
        assertNotNull(view);
    }

    @Test
    public void testValidatePassword() {
        String invalidPassword = "12345";
        String validPassword = "123456";
        assertFalse(mainActivity.validatePassword(invalidPassword));
        assertTrue(mainActivity.validatePassword(validPassword));
    }

    @Test
    public void testValidateEmail() {
        String validEmail = "joebloggs@hotmail.com";
        String invalidEmail = "joebloggsemail.co";
        assertTrue(mainActivity.validateEmail(validEmail));
        assertFalse(mainActivity.validateEmail(invalidEmail));
    }

    // Stop Activity
    @After
    public void tearDown() {
        mainActivity = null;
    }
}