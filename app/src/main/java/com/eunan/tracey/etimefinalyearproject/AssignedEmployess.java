package com.eunan.tracey.etimefinalyearproject;

public class AssignedEmployess {

    private String name;
    private String key;

    public AssignedEmployess(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public AssignedEmployess() {
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
        return "AssignedEmployess{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
