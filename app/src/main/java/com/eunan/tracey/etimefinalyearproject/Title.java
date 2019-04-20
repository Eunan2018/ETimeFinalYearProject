package com.eunan.tracey.etimefinalyearproject;

public enum Title {
    EMPLOYER("Employer"),EMPLOYEE("Employee");

    private String value;

    Title(final String value) {
        this.value = value;
    }

    public String getTitle() {
        return this.value;
    }

}
