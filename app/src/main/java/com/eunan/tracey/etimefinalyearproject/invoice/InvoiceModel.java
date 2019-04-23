package com.eunan.tracey.etimefinalyearproject.invoice;

public class InvoiceModel {

    private String project;
    private String hrs;
    private String rate;
    private String total;


    public InvoiceModel( String project, String hrs, String rate, String total) {
        this.project = project;
        this.hrs = hrs;
        this.rate = rate;
        this.total = total;
    }

    public InvoiceModel() {
    }


    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getHrs() {
        return hrs;
    }

    public void setHrs(String hrs) {
        this.hrs = hrs;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "InvoiceModel{" +
                ", project='" + project + '\'' +
                ", hrs='" + hrs + '\'' +
                ", rate='" + rate + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
