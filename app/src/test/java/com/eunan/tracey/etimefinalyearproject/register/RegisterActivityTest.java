package com.eunan.tracey.etimefinalyearproject.register;

import com.eunan.tracey.etimefinalyearproject.main.MainActivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterActivityTest {
    RegisterActivity registerActivity; RegisterActivity registerActivity2;

    @Before
    public void setUp() throws Exception {
        registerActivity = new RegisterActivity();
        registerActivity2 = new RegisterActivity();
    }
    @Test
    public void validatePassword() {
        String validPassword = "123456";
        String invalidPassword = "12345";
        assertTrue(registerActivity.validatePassword(validPassword));
        assertFalse(registerActivity.validatePassword(invalidPassword));
    }

    @Test
    public void validateEmail() {
        String validEmail = "eunantracey@hotmail.com";
        String invalidEmail = "joebloggsemail.co";
        assertTrue(registerActivity2.validateEmail(validEmail));
        assertFalse(registerActivity2.validateEmail(invalidEmail));
    }


}