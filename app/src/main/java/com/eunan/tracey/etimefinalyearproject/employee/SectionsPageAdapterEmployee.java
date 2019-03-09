package com.eunan.tracey.etimefinalyearproject.employee;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eunan.tracey.etimefinalyearproject.invoice.InvoiceFragment;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetFragment;

public class SectionsPageAdapterEmployee extends FragmentPagerAdapter {


    public SectionsPageAdapterEmployee(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               TimeSheetFragment timesheetFragment = new TimeSheetFragment();
                return timesheetFragment;
            case 1:
                InvoiceFragment invoiceFragment = new InvoiceFragment();
                return invoiceFragment;
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
                return "TIME SHEET";
            case 1:
                return "INVOICE";
            default:
                return null;
        }
    }
}
