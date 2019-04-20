package com.eunan.tracey.etimefinalyearproject;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.eunan.tracey.etimefinalyearproject.register.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class RegisterTest {

    @Rule
    public ActivityTestRule<RegisterActivity> rule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void init(){
        rule.getActivity();
    }

    @Test
    public void isDisplayedTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.text_view_reg_login)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_reg_email)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_reg_password)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_reg_user_name)).check(matches(isDisplayed()));
        onView(withId(R.id.radio_group_reg)).check(matches(isDisplayed()));
        onView(withId(R.id.button_sign_up)).check(matches(isDisplayed()));
        Thread.sleep(2000);

    }

    @Test
    public void registerTesting() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("winnerofcoding@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reg_user_name)).perform(typeText("Winteris"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.radio_button_employer)).perform(click());
        onView(withId(R.id.button_sign_up)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void allReadyRegisteredTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("greengo@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reg_user_name)).perform(typeText("Jay"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.radio_button_employer)).perform(click());
        onView(withId(R.id.button_sign_up)).perform(click());
        onView(withText(R.string.registration_failed)).inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

}
