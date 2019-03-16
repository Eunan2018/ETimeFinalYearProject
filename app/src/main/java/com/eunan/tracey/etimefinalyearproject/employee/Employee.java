package com.eunan.tracey.etimefinalyearproject.employee;

public class Employee {

    private String name;
    private String date;
    private String project;
    private String employerKey;


    public Employee(String name, String date, String project, String employerKey) {
        this.name = name;
        this.date = date;
        this.project = project;
        this.employerKey = employerKey;
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

    public String getEmployerKey() {
        return employerKey;
    }

    public void setEmployerKey(String employerKey) {
        this.employerKey = employerKey;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", project='" + project + '\'' +
                ", employerKey='" + employerKey + '\'' +
                '}';
    }
}
