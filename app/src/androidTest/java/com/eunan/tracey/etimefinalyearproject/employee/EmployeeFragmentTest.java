package com.eunan.tracey.etimefinalyearproject.employee;

import android.support.constraint.ConstraintLayout;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.eunan.tracey.etimefinalyearproject.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeFragmentTest {

    @Rule
    public ActivityTestRule<EmployeeProfileActivity> employeeTestRule =
            new ActivityTestRule<>(EmployeeProfileActivity.class);

    private EmployeeProfileActivity activity = null;

    @Before
    public void setUp() throws Exception {
        activity = employeeTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        ConstraintLayout container = activity.findViewById(R.id.emp);
        assertNotNull(container);
    }

    @After
    public void tearDown() throws Exception {
        activity = null;
    }
}