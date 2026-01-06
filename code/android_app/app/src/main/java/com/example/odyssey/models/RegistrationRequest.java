package com.example.odyssey.models;

public class RegistrationRequest {
    private String name;
    private String email;
    private String mobileNumber;
    private String role;
    private String password;

    public RegistrationRequest(String name, String email, String mobileNumber, String role, String password) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}
