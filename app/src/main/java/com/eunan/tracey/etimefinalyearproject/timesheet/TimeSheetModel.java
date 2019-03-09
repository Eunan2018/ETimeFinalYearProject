package com.eunan.tracey.etimefinalyearproject.timesheet;

public class TimeSheetModel {

   // private ProjectModel project;
    private String project;
    private String hours;
    private String comments;
    private String day;
   // private Weekday day;

    public TimeSheetModel(String project, String hours, String comments, String day) {
        this.project = project;
        this.hours = hours;
        this.comments = comments;
        this.day = day;
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

//    public Weekday getDay() {
//        return day;
//    }
//
//    public void setDay(Weekday day) {
//        this.day = day;
//    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "TimeSheetModel{" +
                "project=" + project +
                ", hours=" + hours +
                ", comments='" + comments + '\'' +
                ", day=" + day +
                '}';
    }

}

