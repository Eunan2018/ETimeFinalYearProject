package com.eunan.tracey.etimefinalyearproject;

import java.util.List;

public class Project {

    private String name;
    private String location;

    private List<Employee> employeeList;

    public Project() {
    }

    public Project(String name, String location, List<Employee> employeeList) {
        this.name = name;
        this.location = location;
        this.employeeList = employeeList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }
}
