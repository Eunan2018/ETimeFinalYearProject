
package com.eunan.tracey.etimefinalyearproject.salary;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SalaryCalculatorTest {
    @Test
    public void calculateSalary(){
        double hours = 12;
        double rate = 12.50;
        int taxCode = 1;
        double resultPass = 120.0;
        double resultFail = 150.0;
        assertTrue(Math.abs(SalaryCalculator.calculateSalary(hours,rate,taxCode)-resultPass) == 0);
        assertFalse(Math.abs(SalaryCalculator.calculateSalary(hours,rate,taxCode)-resultFail) == 0);
    }
    
}