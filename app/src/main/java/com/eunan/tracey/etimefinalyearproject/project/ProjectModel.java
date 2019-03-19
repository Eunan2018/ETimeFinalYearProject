package com.eunan.tracey.etimefinalyearproject.project;

import com.eunan.tracey.etimefinalyearproject.AssignedEmployess;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectModel {

    //private String projectId;
    private String projectName;
    private String projectLocation;
    private int projectTimestamp;
    private Map<String,AssignedEmployess> assignedEmployessList;

    public ProjectModel(String projectName, String projectLocation,int projectTimestamp, Map<String,AssignedEmployess> assignedEmployessList) {

        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.assignedEmployessList = assignedEmployessList;
        this.projectTimestamp = projectTimestamp;
    }


    public ProjectModel() {

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


    public int getProjectTimestamp() {
        return projectTimestamp;
    }

    public void setProjectTimestamp(int projectTimestamp) {
        this.projectTimestamp = projectTimestamp;
    }

    public Map<String, AssignedEmployess> getAssignedEmployessList() {
        return assignedEmployessList;
    }

    public void setAssignedEmployessList(Map<String, AssignedEmployess> assignedEmployessList) {
        this.assignedEmployessList = assignedEmployessList;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "projectName='" + projectName + '\'' +
                ", projectLocation='" + projectLocation + '\'' +
                ", projectTimestamp=" + projectTimestamp +
                ", assignedEmployessList=" + assignedEmployessList +
                '}';
    }


}
