package com.eunan.tracey.etimefinalyearproject.salary;

public class SalaryModel {

    private String name;
    private String email;
    private String hourlyRate;
    private String taxCode;
    private String niNum;


    public SalaryModel(String name, String email, String hourlyRate, String taxCode, String niNum) {
        this.name = name;
        this.email = email;
        this.hourlyRate = hourlyRate;
        this.taxCode = taxCode;
        this.niNum = niNum;
    }

    public SalaryModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getNiNum() {
        return niNum;
    }

    public void setNiNum(String niNum) {
        this.niNum = niNum;
    }

    @Override
    public String toString() {
        return "SalaryModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", hourlyRate='" + hourlyRate + '\'' +
                ", taxCode='" + taxCode + '\'' +
                ", niNum='" + niNum + '\'' +
                '}';
    }
}
