package com.example.odyssey.models;

public class UploadVehicleResponse {
    private String status;
    private String message;

    public UploadVehicleResponse(String status, String message) {
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
