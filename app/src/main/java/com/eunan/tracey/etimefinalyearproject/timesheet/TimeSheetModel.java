package com.eunan.tracey.etimefinalyearproject.timesheet;

public class TimeSheetModel {

   // private ProjectModel project;
    private String project;
    private String hours;
    private String comments;

   // private Weekday day;

    public TimeSheetModel(String project, String hours, String comments) {
        this.project = project;
        this.hours = hours;
        this.comments = comments;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    @Override
    public String toString() {
        return "TimeSheetModel{" +
                "project='" + project + '\'' +
                ", hours='" + hours + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}

