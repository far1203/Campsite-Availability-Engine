package com.farhan.stockalertengine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CampsiteSubscriptionRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Campground name is required")
    private String campgroundName;

    @NotBlank(message = "Target date is required")
    private String targetDate;

    @NotBlank(message = "State abbreviation is required")
    private String state;

    @NotBlank(message = "Campsite number is required")
    private String campsiteNumber;


    public CampsiteSubscriptionRequest() {
    }

    public CampsiteSubscriptionRequest(String email, String campgroundName, String state, String campsiteNumber, String targetDate) {
        this.email = email;
        this.campgroundName = campgroundName;
        this.state = state;
        this.campsiteNumber = campsiteNumber;
        this.targetDate = targetDate;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCampgroundName() {
        return campgroundName;
    }

    public void setCampgroundName(String campgroundName) {
        this.campgroundName = campgroundName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCampsiteNumber() {
        return campsiteNumber;
    }

    public void setCampsiteNumber(String campsiteNumber) {
        this.campsiteNumber = campsiteNumber;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }
}