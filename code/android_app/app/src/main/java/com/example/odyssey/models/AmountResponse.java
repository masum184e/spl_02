package com.example.odyssey.models;

public class AmountResponse {
    private String distance;
    private String duration;
    private String total_price;
    private String status;
    private String message;

    public AmountResponse(String status, String message,String distance, String duration, String total_price) {
        this.status=status;
        this.message=message;
        this.distance = distance;
        this.duration = duration;
        this.total_price = total_price;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getTotal_price() {
        return total_price;
    }
}
