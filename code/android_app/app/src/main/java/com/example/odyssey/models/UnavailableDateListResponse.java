package com.example.odyssey.models;

import java.util.List;

public class UnavailableDateListResponse {
    private String status;
    private String message;
    private List<String> dates;

    public UnavailableDateListResponse(String status, String message, List<String> dates) {
        this.status = status;
        this.message = message;
        this.dates = dates;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDateList() {
        return dates;
    }
}
