package com.example.odyssey.models;


import okhttp3.MultipartBody;

public class UploadVehicleRequest {

    private String license_plate_number;
    private String chasis_number;
    private String number_of_seats;
    private String model;
    private String mileage;
    private String year;
    private String color;
    private String owner_mobile_number;
    private MultipartBody.Part ownerProfile;
    private MultipartBody.Part mainImage;
    private MultipartBody.Part frontImage;
    private MultipartBody.Part backImage;
    private MultipartBody.Part leftImage;
    private MultipartBody.Part rightImage;
    private MultipartBody.Part interiorImage;

    public UploadVehicleRequest(String license_plate_number, String chasis_number, String number_of_seats, String model, String mileage, String year, String color, String owner_mobile_number, MultipartBody.Part ownerProfile, MultipartBody.Part mainImage, MultipartBody.Part frontImage, MultipartBody.Part backImage, MultipartBody.Part leftImage, MultipartBody.Part rightImage, MultipartBody.Part interiorImage) {
        this.license_plate_number = license_plate_number;
        this.chasis_number = chasis_number;
        this.number_of_seats = number_of_seats;
        this.model = model;
        this.mileage = mileage;
        this.year = year;
        this.color = color;
        this.owner_mobile_number = owner_mobile_number;
        this.ownerProfile = ownerProfile;
        this.mainImage = mainImage;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.interiorImage = interiorImage;
    }

    public String getLicense_plate_number() {
        return license_plate_number;
    }

    public String getChasis_number() {
        return chasis_number;
    }

    public String getNumber_of_seats() {
        return number_of_seats;
    }

    public String getModel() {
        return model;
    }

    public String getMileage() {
        return mileage;
    }

    public String getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public String getOwner_mobile_number() {
        return owner_mobile_number;
    }

    public MultipartBody.Part getOwnerProfile() {
        return ownerProfile;
    }

    public MultipartBody.Part getMainImage() {
        return mainImage;
    }

    public MultipartBody.Part getFrontImage() {
        return frontImage;
    }

    public MultipartBody.Part getBackImage() {
        return backImage;
    }

    public MultipartBody.Part getLeftImage() {
        return leftImage;
    }

    public MultipartBody.Part getRightImage() {
        return rightImage;
    }

    public MultipartBody.Part getInteriorImage() {
        return interiorImage;
    }
}
