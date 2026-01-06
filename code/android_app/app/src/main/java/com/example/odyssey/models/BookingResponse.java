package com.example.odyssey.models;

public class BookingResponse {
    private String status;
    private String message;

    public BookingResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
