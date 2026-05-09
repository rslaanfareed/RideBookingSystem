package com.ridebooking.model;

public class Driver {
    private String driverId;
    private String name;
    private String email;
    private String phone;
    private String vehicleNumber;
    private RideType rideType;
    private double rating;
    private double ratingSum;
    private int ratingCount;
    private boolean available;
    private double earningsBalance;

    public Driver(String driverId, String name, String email, String phone, 
                  String vehicleNumber, RideType rideType) {
        this.driverId = driverId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vehicleNumber = vehicleNumber;
        this.rideType = rideType;
        this.rating = 4.5;
        this.ratingSum = 4.5;
        this.ratingCount = 1;
        this.available = true;
        this.earningsBalance = 0.0;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public RideType getRideType() {
        return rideType;
    }

    public double getRating() {
        return rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getEarningsBalance() {
        return earningsBalance;
    }

    public void addEarnings(double amount) {
        this.earningsBalance += amount;
    }

    public void addRating(double newRating) {
        this.ratingSum += newRating;
        this.ratingCount += 1;
        this.rating = this.ratingSum / this.ratingCount;
    }

    public void updateRating(double newRating) {
        this.rating = newRating;
        this.ratingSum = newRating;
        this.ratingCount = 1;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId='" + driverId + '\'' +
                ", name='" + name + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", rideType=" + rideType +
                ", rating=" + rating +
                ", available=" + available +
                ", earningsBalance=" + earningsBalance +
                '}';
    }
}
