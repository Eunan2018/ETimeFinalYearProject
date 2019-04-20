package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.eunan.tracey.etimefinalyearproject.user.UsersActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class EmployerTest {

    public final ActivityTestRule<UsersActivity> myActivityTestRule = new ActivityTestRule<>(UsersActivity.class, false, false);

    @Before
    public void setUp() {
        myActivityTestRule.launchActivity(new Intent());
    }

    @Test
    public void sendProjectRequestTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_users)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.button_profile_project_request)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.button_profile_project_request)).perform(click());
    }



}
