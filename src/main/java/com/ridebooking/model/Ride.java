package com.ridebooking.model;

public class Ride {
    private String rideId;
    private Passenger passenger;
    private Driver driver;
    private String pickupLocation;
    private String dropoffLocation;
    private RideType rideType;
    private RideStatus status;
    private double fare;
    private long createdTime;
    private long completedTime;

    public Ride(String rideId, Passenger passenger, String pickupLocation, 
                String dropoffLocation, RideType rideType) {
        this.rideId = rideId;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.rideType = rideType;
        this.status = RideStatus.REQUESTED;
        this.createdTime = System.currentTimeMillis();
    }

    public String getRideId() {
        return rideId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public RideType getRideType() {
        return rideType;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "rideId='" + rideId + '\'' +
                ", passenger=" + passenger.getName() +
                ", driver=" + (driver != null ? driver.getName() : "Not Assigned") +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropoffLocation='" + dropoffLocation + '\'' +
                ", rideType=" + rideType +
                ", status=" + status +
                ", fare=" + fare +
                '}';
    }
}
