package com.eunan.tracey.etimefinalyearproject.employee;

public class Employee {

    private String name;
    private String date;

    public Employee(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public Employee() {
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

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
