package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class AddEmployeeToProject {
    private final ActivityTestRule<EmployerProfileActivity> rule =
            new ActivityTestRule<>(EmployerProfileActivity.class, false, false);

    @Before
    public void setUp() {
        rule.launchActivity(new Intent());
    }

    @Test
    public void addEmployeeFromProject() throws InterruptedException {

        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_project)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        onView(withId(R.id.button_add_emplyee_mp)).perform(click());
                Thread.sleep(2000);
        onView(withId(R.id.recycler_view_add_employee)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        onView(withId(R.id.button_update_add_employee)).perform(click());
        Thread.sleep(2000);
    }
}
