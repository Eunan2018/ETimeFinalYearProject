package com.eunan.tracey.etimefinalyearproject.employee;

public class AssignedProjects {

    private String name;
    private int timestamp;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public AssignedProjects(String name, int timestamp) {
        this.name = name;

        this.timestamp = timestamp;

    }

    public AssignedProjects() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "AssignedProjects{" +
                "name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
