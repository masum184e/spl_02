package com.example.odyssey.models;

import java.util.List;

public class VehicleModel {
    private int vehicle_id;
    private int driver_id;
    private String type;
    private String license_plate_number;
    private double mileage;
    private int number_of_seats;
    private String chasis_number;
    private String model;
    private int year;
    private String color;
    private String owner_mobile_number;
    private String owner_image;
    private String main_image;
    private String front_image;
    private String back_image;
    private String left_image;
    private String interior_image;
    private String right_image;
    private String name;
    private String mobile_number;
    private String email;
    private List<String> dates;

    public VehicleModel(int vehicle_id, int driver_id, String type, String license_plate_number, double mileage, int number_of_seats, String chasis_number, String model, int year, String color, String owner_mobile_number, String owner_image, String main_image, String front_image, String back_image, String left_image, String interior_image, String right_image, String name, String mobile_number, String email, List<String> dates) {
        this.vehicle_id = vehicle_id;
        this.driver_id = driver_id;
        this.type = type;
        this.license_plate_number = license_plate_number;
        this.mileage = mileage;
        this.number_of_seats = number_of_seats;
        this.chasis_number = chasis_number;
        this.model = model;
        this.year = year;
        this.color = color;
        this.owner_mobile_number = owner_mobile_number;
        this.owner_image = owner_image;
        this.main_image = main_image;
        this.front_image = front_image;
        this.back_image = back_image;
        this.left_image = left_image;
        this.interior_image = interior_image;
        this.right_image = right_image;
        this.name = name;
        this.mobile_number = mobile_number;
        this.email = email;
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobile_number;
    }

    public String getEmail() {
        return email;
    }

    public int getVehicleId() {
        return vehicle_id;
    }

    public int getDriverId() {
        return driver_id;
    }

    public String getType() {
        return type;
    }

    public String getLicensePlateNumber() {
        return license_plate_number;
    }

    public double getMileage() {
        return mileage;
    }

    public int getNumberOfSeats() {
        return number_of_seats;
    }

    public String getChasisNumber() {
        return chasis_number;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public String getOwnerMobileNumber() {
        return owner_mobile_number;
    }

    public String getOwnerImage() {
        return owner_image;
    }

    public String getMainImage() {
        return main_image;
    }

    public String getFrontImage() {
        return front_image;
    }

    public String getBackImage() {
        return back_image;
    }

    public String getLeftImage() {
        return left_image;
    }

    public String getInteriorImage() {
        return interior_image;
    }

    public String getRightImage() {
        return right_image;
    }
}
