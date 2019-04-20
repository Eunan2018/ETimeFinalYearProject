package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class TimesheetTest {

    public final ActivityTestRule<EmployeeProfileActivity> myActivityTestRule = new ActivityTestRule<>(EmployeeProfileActivity.class, false, false);

    @Before
    public void setUp() {
        myActivityTestRule.launchActivity(new Intent());
    }

    @Test
    public void displayViews() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.button_submit)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_monday_day)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_tuesday_day)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_wednesday_day)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_thursday_day)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_friday_day)).check(matches(isDisplayed()));
    //    onView(withId(R.id.button_submit)).check(matches(isDisplayed()));

        Thread.sleep(2000);
    }

    @Test
    public void createTimesheet() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.text_view_monday_day)).perform(click());
        Thread.sleep(2000);
        selectProject();
        onView(withId(R.id.text_view_tuesday_day)).perform(click());
        Thread.sleep(2000);
        selectProject();
        onView(withId(R.id.text_view_wednesday_day)).perform(click());
        Thread.sleep(2000);
        selectProject();
        onView(withId(R.id.text_view_thursday_day)).perform(click());
        Thread.sleep(2000);
        selectProject();
        onView(withId(R.id.text_view_friday_day)).perform(click());
        Thread.sleep(2000);
        selectProject();
        Thread.sleep(2000);
        onView(withId(R.id.button_submit)).perform(click());
    }


    public void selectProject() {
        onView(withId(R.id.spinner_hours)).perform(click());
        onData(allOf(is(instanceOf(String.class)))).atPosition(8).perform(click());
        onView(withId(R.id.recycler_view_ts_builder)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.button_done_ts)).perform(click());
    }
}
