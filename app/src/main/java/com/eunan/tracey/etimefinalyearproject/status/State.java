package com.eunan.tracey.etimefinalyearproject.status;

public enum State {
    EMPLOYED("employed"),NOT_EMPLOYED("notEmployed"),RECEIVED("received"),SENT("sent");

    private String value;

    State(final String value) {
        this.value = value;
    }
}
