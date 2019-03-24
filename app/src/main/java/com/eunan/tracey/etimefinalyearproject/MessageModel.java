package com.eunan.tracey.etimefinalyearproject;



public class MessageModel {
    private String message;
    private String name;

    public MessageModel(String message,String name) {
        this.message = message;
        this.name = name;
    }

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "message='" + message + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

