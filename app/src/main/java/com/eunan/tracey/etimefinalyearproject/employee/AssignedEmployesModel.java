package com.eunan.tracey.etimefinalyearproject.employee;

public class AssignedEmployesModel {

    private String name;
    private String key;

    public AssignedEmployesModel(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public AssignedEmployesModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "AssignedEmployesModel{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
