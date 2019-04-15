package com.eunan.tracey.etimefinalyearproject.History;

public class HistoryModel {

    private String day;
    private String hours;
    private String project;

    public HistoryModel(String day, String hours, String project) {
        this.day = day;
        this.hours = hours;
        this.project = project;
    }

    public HistoryModel() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "HistoryModel{" +
                "day='" + day + '\'' +
                ", hours='" + hours + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}

