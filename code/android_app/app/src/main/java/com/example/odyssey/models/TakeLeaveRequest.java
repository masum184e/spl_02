package com.example.odyssey.models;

public class TakeLeaveRequest {
    private String start_date;
    private String end_date;

    public TakeLeaveRequest(String start_date, String end_date) {
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }
}
