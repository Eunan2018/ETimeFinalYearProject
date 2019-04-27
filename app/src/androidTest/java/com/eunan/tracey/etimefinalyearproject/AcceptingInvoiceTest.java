package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class AcceptingInvoiceTest {

    private final ActivityTestRule<EmployerProfileActivity> rule =
            new ActivityTestRule<>(EmployerProfileActivity.class, false, false);

    @Before
    public void setUp() {
        rule.launchActivity(new Intent());
    }

    @Test
    public void acceptTimesheet() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.employer_tab_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_employees_emp)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        onView(withText("Invoice")).inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.button_accept_inv)).perform(click());
    }
}
