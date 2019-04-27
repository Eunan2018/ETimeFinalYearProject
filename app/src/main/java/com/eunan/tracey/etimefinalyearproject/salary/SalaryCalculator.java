package com.eunan.tracey.etimefinalyearproject.salary;

import java.text.DecimalFormat;

public class SalaryCalculator {

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public static double calculateSalary(double hours, double rate, int tax) {

        double pay = 0.0;

        switch (tax) {
            case 1:
                pay = ( hours * rate) * .80;
                break;
            case 2:
                pay = (hours * rate) * .77;
                break;
            default:
                break;
        }
        return Double.parseDouble(df2.format(pay));
    }


}
