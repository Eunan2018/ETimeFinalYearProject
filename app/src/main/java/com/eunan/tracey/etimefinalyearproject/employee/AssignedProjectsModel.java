package com.eunan.tracey.etimefinalyearproject.employee;

public class AssignedProjectsModel {

    private String name;
    private int timestamp;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public AssignedProjectsModel(String name, int timestamp) {
        this.name = name;

        this.timestamp = timestamp;

    }

    public AssignedProjectsModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "AssignedProjectsModel{" +
                "name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
