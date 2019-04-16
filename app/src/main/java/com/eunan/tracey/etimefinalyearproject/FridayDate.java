package com.eunan.tracey.etimefinalyearproject;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FridayDate {

    @SuppressLint("SimpleDateFormat")
   static DateFormat displayDateFormat;



    public static String fridayDate(){
        Calendar friday = Calendar.getInstance();
        friday.setTime(new Date());
        friday.set(Calendar.DAY_OF_MONTH, friday.get(Calendar.DAY_OF_MONTH)-38);
        displayDateFormat = new SimpleDateFormat("EEE, MMM d", Locale.UK);
        return displayDateFormat.format(friday.getTime());

    }
}
