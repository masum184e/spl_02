package com.example.odyssey.models;

import java.util.List;

public class VehicleListResponse {
    private String status;
    private String message;
    private List<VehicleModel> data;

    public VehicleListResponse(String status, String message, List<VehicleModel> data) {
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

    public List<VehicleModel> getData() {
        return data;
    }
}
