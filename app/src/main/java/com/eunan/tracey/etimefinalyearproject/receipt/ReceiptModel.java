package com.eunan.tracey.etimefinalyearproject.receipt;

public class ReceiptModel {

    private String projectName;
    private String basePay;
    private String hrsWorked;
    private String totalPay;
    private String email;

    public ReceiptModel(String projectName, String basePay, String hrsWorked,String totalPay,String email) {
        this.projectName = projectName;
        this.basePay = basePay;
        this.hrsWorked = hrsWorked;
        this.totalPay = totalPay;
        this.email = email;
    }

    public ReceiptModel() {
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBasePay() {
        return basePay;
    }

    public void setBasePay(String basePay) {
        this.basePay = basePay;
    }

    public String getHrsWorked() {
        return hrsWorked;
    }

    public void setHrsWorked(String hrsWorked) {
        this.hrsWorked = hrsWorked;
    }


    public String getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(String totalPay) {
        this.totalPay = totalPay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
