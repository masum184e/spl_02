package com.example.odyssey.models;

public class ProfileResponse {
    private String status;
    private String message;
    private ProfileModel data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ProfileModel getData() {
        return data;
    }
}
