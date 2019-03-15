package com.eunan.tracey.etimefinalyearproject.employee;

public class EmployeeModel {

    public String date;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmployeeModel(){

    }

    public EmployeeModel(String date){
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
