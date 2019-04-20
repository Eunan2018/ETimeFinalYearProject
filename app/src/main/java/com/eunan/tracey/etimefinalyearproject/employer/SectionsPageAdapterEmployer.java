package com.eunan.tracey.etimefinalyearproject.employer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eunan.tracey.etimefinalyearproject.Fragments.EmployeeFragment;
import com.eunan.tracey.etimefinalyearproject.Fragments.ProjectFragment;

import java.util.Stack;

public class SectionsPageAdapterEmployer extends FragmentPagerAdapter {

    public SectionsPageAdapterEmployer(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProjectFragment();
            case 1:
                return new EmployeeFragment();
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
