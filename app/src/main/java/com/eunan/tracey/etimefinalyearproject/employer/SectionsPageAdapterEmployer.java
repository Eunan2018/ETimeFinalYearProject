package com.eunan.tracey.etimefinalyearproject.employer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeFragment;
import com.eunan.tracey.etimefinalyearproject.project.ProjectFragment;

public class SectionsPageAdapterEmployer extends FragmentPagerAdapter {

    public SectionsPageAdapterEmployer(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               ProjectFragment projectFragment = new ProjectFragment();
                return projectFragment;
            case 1:
                EmployeeFragment employeeFragment = new EmployeeFragment();
                return employeeFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch(position){
            case 0:
                return "PROJECT";
            case 1:
                return "EMPLOYEE";
            default:
                return null;
        }
    }
}
