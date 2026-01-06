package com.example.odyssey.models;

import java.util.List;

public class VehicleResponse {
    private String status;
    private String message;
    private VehicleModel data;

    public VehicleResponse(String status, String message, VehicleModel data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public VehicleModel getData() {
        return data;
    }
}
