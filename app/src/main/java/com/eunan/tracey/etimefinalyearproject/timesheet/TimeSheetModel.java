package com.eunan.tracey.etimefinalyearproject.timesheet;

public class TimeSheetModel {

   // private ProjectModel project;
    private String project;
    private String hours;
    private String minutes;

    public TimeSheetModel(String project, String hours, String minutes) {
        this.project = project;
        this.hours = hours;
        this.minutes = minutes;

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

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "TimeSheetModel{" +
                "project='" + project + '\'' +
                ", hours='" + hours + '\'' +
                ", minutes='" + minutes + '\'' +
                '}';
    }
}

