package com.example.odyssey.models;

public class CarReviewItemModel {
    private String userName, daysAgo, review;
    private int image;

    public CarReviewItemModel(String userName, String daysAgo, String review, int image) {
        this.userName = userName;
        this.daysAgo = daysAgo;
        this.review = review;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDaysAgo() {
        return daysAgo;
    }

    public void setDaysAgo(String daysAgo) {
        this.daysAgo = daysAgo;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
