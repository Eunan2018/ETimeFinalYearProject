package com.eunan.tracey.etimefinalyearproject.employee;

public class EmployeeProjectModel {

    private String key;
    private String project;

    public EmployeeProjectModel(String key, String project) {
        this.key = key;
        this.project = project;
    }

    public EmployeeProjectModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "EmployeeProjectModel{" +
                "key='" + key + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
