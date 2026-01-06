package com.example.odyssey.models;

import java.util.List;

public class BookingListResponse {
    private String status;

    private String message;

    private List<BookingModel> data;

    public BookingListResponse(String status, String message, List<BookingModel> data) {
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

    public List<BookingModel> getData() {
        return data;
    }
}
