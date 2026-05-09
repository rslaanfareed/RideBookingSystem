package com.ridebooking.observer;

import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;

public class PassengerObserver implements RideStatusObserver {
    private Passenger passenger;

    public PassengerObserver(Passenger passenger) {
        this.passenger = passenger;
    }

    @Override
    public void update(Ride ride, String message) {
        System.out.println("\n[NOTIFICATION for " + passenger.getName() + "] " + message);
        System.out.println("  Ride ID: " + ride.getRideId());
        System.out.println("  Status: " + ride.getStatus().getDescription());
        if (ride.getDriver() != null) {
            System.out.println("  Driver: " + ride.getDriver().getName() + 
                              " (" + ride.getDriver().getVehicleNumber() + ")");
        }
        System.out.println("  Fare: " + ride.getFare());
    }
}
