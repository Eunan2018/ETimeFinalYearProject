package com.eunan.tracey.etimefinalyearproject.project;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.user.UsersActivity;

import org.junit.Rule;
import org.junit.Test;

public class MaintainProjectTest {

    @Rule
    public ActivityTestRule<UsersActivity> activityTestRule = new ActivityTestRule<>(UsersActivity.class);

    @Test
    public void scrollToPosition(){
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_users))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2,ViewActions.click()));    }
}