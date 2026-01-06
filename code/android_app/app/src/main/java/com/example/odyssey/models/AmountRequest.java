package com.example.odyssey.models;

public class AmountRequest {
    private String driver_id;
    private String pickup_datetime;
    private String dropoff_datetime;
    private String pickup_location;
    private String dropoff_location;

    public AmountRequest(String driver_id, String pickup_datetime, String dropoff_datetime, String pickup_location, String dropoff_location) {
        this.driver_id = driver_id;
        this.pickup_datetime = pickup_datetime;
        this.dropoff_datetime = dropoff_datetime;
        this.pickup_location = pickup_location;
        this.dropoff_location = dropoff_location;
    }

    public String getDriver_id() {
        return driver_id;
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
}
