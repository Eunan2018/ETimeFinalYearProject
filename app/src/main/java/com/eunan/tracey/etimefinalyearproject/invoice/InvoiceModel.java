package com.eunan.tracey.etimefinalyearproject.invoice;

public class InvoiceModel {

    private String name;
    private String date;
    private String project;
    private String hrs;
    private String rate;
    private String total;


    public InvoiceModel(String name, String date, String project, String hrs, String rate, String total) {
        this.name = name;
        this.date = date;
        this.project = project;
        this.hrs = hrs;
        this.rate = rate;
        this.total = total;
    }

    public InvoiceModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", project='" + project + '\'' +
                ", hrs='" + hrs + '\'' +
                ", rate='" + rate + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
