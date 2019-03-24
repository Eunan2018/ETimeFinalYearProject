package com.eunan.tracey.etimefinalyearproject.employer;

public class EmployerWeek {

    private String day;
    private String hours;
    private String projectt;

    public EmployerWeek(String day, String hours, String projectt) {
        this.day = day;
        this.hours = hours;
        this.projectt = projectt;
    }

    public EmployerWeek() {
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

    public String getProjectt() {
        return projectt;
    }

    public void setProjectt(String projectt) {
        this.projectt = projectt;
    }

    @Override
    public String toString() {
        return "EmployerWeek{" +
                "day='" + day + '\'' +
                ", hours='" + hours + '\'' +
                ", projectt='" + projectt + '\'' +
                '}';
    }
}
