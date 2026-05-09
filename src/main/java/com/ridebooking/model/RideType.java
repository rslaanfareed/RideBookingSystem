package com.ridebooking.model;

public enum RideType {
    STANDARD(100, "Standard Ride"),
    PREMIUM(150, "Premium Ride");

    private final double baseRate;
    private final String description;

    RideType(double baseRate, String description) {
        this.baseRate = baseRate;
        this.description = description;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public String getDescription() {
        return description;
    }
}
