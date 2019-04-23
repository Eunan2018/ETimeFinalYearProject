package com.eunan.tracey.etimefinalyearproject.main;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {

    MainActivity mainActivity;

    @Before
    public void setUp(){
        mainActivity = new MainActivity();
    }

    @Test
    public void isValidPassword() {
        String validPassword1 = "123456";
        String validPassword2 = "xyz123hytew";
        String validPassword3 = "dogandacats";
        assertTrue(mainActivity.validatePassword(validPassword1));
        assertTrue(mainActivity.validatePassword(validPassword2));
        assertTrue(mainActivity.validatePassword(validPassword3));
    }

    @Test
    public void isInvalidPassword() {
        String invalidPassword1 = "12345";
        String invalidPassword2 = "zxcde";
        String invalidPassword3= "124";
        assertFalse(mainActivity.validatePassword(invalidPassword1));
        assertFalse(mainActivity.validatePassword(invalidPassword2));
        assertFalse(mainActivity.validatePassword(invalidPassword3));
    }

    @Test
    public void isValidEmail() {
        String validEmail1 = "eunantracey@hotmail.com";
        String validEmail2 = "john@hotmail.com";
        String validEmail3 = "xyz@hotmail.com";
        assertTrue(mainActivity.validateEmail(validEmail1));
        assertTrue(mainActivity.validateEmail(validEmail2));
        assertTrue(mainActivity.validateEmail(validEmail3));
    }

    @Test
    public void isInvalidEmail() {
        String inValidEmail1 = "@hotmail.com";
        String inValidEmail2 = "eunantracey.com";
        String inValidEmail3 = "joebloggsemail.co";
        assertFalse(mainActivity.validateEmail(inValidEmail1));
        assertFalse(mainActivity.validateEmail(inValidEmail2));
        assertFalse(mainActivity.validateEmail(inValidEmail3));
    }
}