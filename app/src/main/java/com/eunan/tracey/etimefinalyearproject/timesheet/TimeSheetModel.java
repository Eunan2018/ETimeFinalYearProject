package com.eunan.tracey.etimefinalyearproject.timesheet;

public class TimeSheetModel {

   // private ProjectModel project;
    private String project;
    private String hours;

    public TimeSheetModel(String project, String hours) {
        this.project = project;
        this.hours = hours;

    }

    public TimeSheetModel() {
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }


    @Override
    public String toString() {
        return "TimeSheetModel{" +
                "project='" + project + '\'' +
                ", hours='" + hours + '\'' +
                '}';
    }
}

