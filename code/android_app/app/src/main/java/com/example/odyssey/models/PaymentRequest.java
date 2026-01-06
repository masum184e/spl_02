package com.example.odyssey.models;

public class PaymentRequest {
    private String booking_id;

    public String getBooking_id() {
        return booking_id;
    }

    public PaymentRequest(String booking_id) {
        this.booking_id = booking_id;
    }
}
