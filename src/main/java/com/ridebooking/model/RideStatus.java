package com.ridebooking.model;

public enum RideStatus {
    REQUESTED("Ride Requested"),
    DRIVER_ASSIGNED("Driver Assigned"),
    EN_ROUTE("En Route"),
    ARRIVED("Arrived at Destination"),
    COMPLETED("Ride Completed"),
    CANCELLED("Ride Cancelled");

    private final String description;

    RideStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
