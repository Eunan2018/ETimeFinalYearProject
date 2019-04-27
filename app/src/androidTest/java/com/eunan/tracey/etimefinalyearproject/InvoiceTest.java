package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class InvoiceTest {

    public final ActivityTestRule<EmployeeProfileActivity> rule =
            new ActivityTestRule<>(EmployeeProfileActivity.class, false, false);

    @Before
    public void setUp() {

        rule.launchActivity(new Intent());
    }

    @Test
    public void createInvoice() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.employee_tab_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.recycler_view_invoice)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.edit_text_hrs_inv)).perform(typeText("40"), closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.edit_text_rate_inv)).perform(typeText("12.50"), closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.button_calculate_inv)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button_send_invoice)).perform(click());
    }
}
