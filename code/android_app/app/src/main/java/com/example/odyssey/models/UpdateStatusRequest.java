package com.example.odyssey.models;

public class UpdateStatusRequest {
    private String booking_id;
    private String new_status;

    public UpdateStatusRequest(String booking_id, String new_status) {
        this.booking_id = booking_id;
        this.new_status = new_status;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public String getNew_status() {
        return new_status;
    }
}
