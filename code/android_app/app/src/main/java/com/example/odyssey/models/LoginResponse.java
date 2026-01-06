package com.example.odyssey.models;

public class LoginResponse {
    private String status;
    private String message;
    private String token;
    private String role;

    public LoginResponse(String status, String message, String token, String role) {
        this.status = status;
        this.message = message;
        this.token = token;
        this.role = role;
    }

    public String isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}