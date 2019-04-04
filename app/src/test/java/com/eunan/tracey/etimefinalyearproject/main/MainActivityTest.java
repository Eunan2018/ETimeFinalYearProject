package com.eunan.tracey.etimefinalyearproject.main;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = new MainActivity();
    }

    @Test
    public void validatePassword() {
        String validPassword = "123456";
        String invalidPassword = "12345";
        assertTrue(mainActivity.validatePassword(validPassword));
        assertFalse(mainActivity.validatePassword(invalidPassword));
    }

    @Test
    public void validateEmail() {
        String validEmail = "eunantracey@hotmail.com";
        String invalidEmail = "joebloggsemail.co";
        assertTrue(mainActivity.validateEmail(validEmail));
        assertFalse(mainActivity.validateEmail(invalidEmail));
    }

}