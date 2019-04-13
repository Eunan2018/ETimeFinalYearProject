package com.eunan.tracey.etimefinalyearproject.salary;

public class SalaryModel {

    private String hourlyRate;
    private String taxCode;

    public SalaryModel(String hourlyRate, String taxCode) {
        this.hourlyRate = hourlyRate;
        this.taxCode = taxCode;
    }

    public SalaryModel() {
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Override
    public String toString() {
        return "SalaryModel{" +
                ", hourlyRate='" + hourlyRate + '\'' +
                ", taxCode='" + taxCode + '\'' +
                '}';
    }
}
