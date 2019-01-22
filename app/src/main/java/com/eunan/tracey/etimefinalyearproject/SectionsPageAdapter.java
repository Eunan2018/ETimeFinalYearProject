package com.eunan.tracey.etimefinalyearproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    public SectionsPageAdapter(FragmentManager fragmentManager) {
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
