package com.example.odyssey.models;

public class BookingModel {
    private int booking_id;

    private int driver_id;

    private int renter_id;

    private String pickup_datetime;

    private String dropoff_datetime;

    private String pickup_location;

    private String dropoff_location;

    private int number_of_passengers;

    private int number_of_stoppages;

    private String booking_status;
    private String name;

    public BookingModel(int booking_id, int driver_id, int renter_id, String pickup_datetime, String dropoff_datetime, String pickup_location, String dropoff_location, int number_of_passengers, int number_of_stoppages, String booking_status, String name) {
        this.booking_id = booking_id;
        this.driver_id = driver_id;
        this.renter_id = renter_id;
        this.pickup_datetime = pickup_datetime;
        this.dropoff_datetime = dropoff_datetime;
        this.pickup_location = pickup_location;
        this.dropoff_location = dropoff_location;
        this.number_of_passengers = number_of_passengers;
        this.number_of_stoppages = number_of_stoppages;
        this.booking_status = booking_status;
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public int getRenter_id() {
        return renter_id;
    }

    public String getPickup_datetime() {
        return pickup_datetime;
    }

    public String getDropoff_datetime() {
        return dropoff_datetime;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public String getDropoff_location() {
        return dropoff_location;
    }

    public int getNumber_of_passengers() {
        return number_of_passengers;
    }

    public int getNumber_of_stoppages() {
        return number_of_stoppages;
    }

    public String getBooking_status() {
        return booking_status;
    }
}
