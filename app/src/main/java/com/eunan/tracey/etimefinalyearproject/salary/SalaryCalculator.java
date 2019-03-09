package com.eunan.tracey.etimefinalyearproject.salary;

public class SalaryCalculator {

    public static double calculateSalary(double hours, double rate, int tax) {

        double pay = 0.0;

        switch (tax) {
            case 1:
                pay = (hours * rate) * .80;
                break;
            case 2:
                pay = (hours * rate) * .77;
                break;
            default:
                break;
        }
        return pay;
    }


}
