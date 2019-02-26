package com.eunan.tracey.etimefinalyearproject;

import java.util.List;
import java.util.Map;

public class Project {

    //private String projectId;
    private String projectName;
    private String projectLocation;
    private String projectDescription;
    private List<Map<String,String>> userList;

    public Project(String projectName, String projectLocation, String projectDescription, List<Map<String,String>> user) {

        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.projectDescription = projectDescription;
        this.userList = user;
    }


    public Project() {

    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<Map<String,String>> getUserList() {
        return userList;
    }

    public void setUserList(List<Map<String,String>> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectName='" + projectName + '\'' +
                ", projectLocation='" + projectLocation + '\'' +
                ", projectDescription='" + projectDescription + '\'' +

                '}';
    }
}
