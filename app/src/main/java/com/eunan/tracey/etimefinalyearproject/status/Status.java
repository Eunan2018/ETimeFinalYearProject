package com.eunan.tracey.etimefinalyearproject.status;

public enum Status {
    EMPLOYED("employed"),NOT_EMPLOYED("notEmployed"),RECEIVED("received"),SENT("sent");

    private String value;

    Status(final String value) {
        this.value = value;
    }
}
