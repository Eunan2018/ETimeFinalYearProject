package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class RemoveEmployeeTest {
    private final ActivityTestRule<EmployerProfileActivity> rule =
            new ActivityTestRule<>(EmployerProfileActivity.class, false, false);

    @Before
    public void setUp() {
        rule.launchActivity(new Intent());
    }

    @Test
    public void removeEmployeeFromProject() throws InterruptedException {

        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_project)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_mp)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        onView(withText("Delete")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);
    }
}
