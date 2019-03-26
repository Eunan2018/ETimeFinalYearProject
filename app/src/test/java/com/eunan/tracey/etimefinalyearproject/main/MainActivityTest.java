package com.eunan.tracey.etimefinalyearproject.main;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainActivityTest {

    MainActivity mainActivity;

    @Before
    public void setUp(){
        mainActivity = new MainActivity();
    }


    @Test
    public void testValidatePassword() {
        String validPassword = "1111111";
        String invalidPassword = "121";
        assertTrue(mainActivity.validatePassword(validPassword));
        assertFalse(mainActivity.validatePassword(invalidPassword));
    }


    @Test
    public void validateEmail() {
        String invalidPassword = "12345";
        String validPassword = "123456";
        assertFalse(mainActivity.validatePassword(invalidPassword));
        assertTrue(mainActivity.validatePassword(validPassword));
    }
}

