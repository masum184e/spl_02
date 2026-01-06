package com.example.odyssey.models;

public class ComplaintsRequest {
    private String title;
    private String message;

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public ComplaintsRequest(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
