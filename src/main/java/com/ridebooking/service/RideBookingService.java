package com.ridebooking.service;

import com.ridebooking.model.*;
import com.ridebooking.factory.RideFactory;
import com.ridebooking.observer.RideStatusObserver;
import java.util.*;

public class RideBookingService {
    private Map<String, Ride> rides;
    private DriverManagementService driverService;
    private FareCalculationService fareService;
    private List<RideStatusObserver> observers;

    public RideBookingService(DriverManagementService driverService, 
                             FareCalculationService fareService) {
        this.rides = new HashMap<>();
        this.driverService = driverService;
        this.fareService = fareService;
        this.observers = new ArrayList<>();
    }

    public void registerObserver(RideStatusObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(RideStatusObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Ride ride, String message) {
        for (RideStatusObserver observer : observers) {
            observer.update(ride, message);
        }
    }

    public Ride requestRide(Passenger passenger, String pickupLocation,
                           String dropoffLocation, RideType rideType) {
        Ride ride = RideFactory.createRide(passenger, pickupLocation, dropoffLocation, rideType);
        rides.put(ride.getRideId(), ride);
        
        System.out.println("\n=== Ride Requested ===");
        System.out.println("Ride ID: " + ride.getRideId());
        System.out.println("Passenger: " + passenger.getName());
        System.out.println("Route: " + pickupLocation + " -> " + dropoffLocation);
        System.out.println("Ride Type: " + rideType.getDescription());
        
        notifyObservers(ride, "Your ride has been requested!");
        return ride;
    }

    public boolean assignDriverToRide(String rideId, double distance) {
        Ride ride = rides.get(rideId);
        if (ride == null || ride.getDriver() != null) {
            return false;
        }

        Driver driver = driverService.assignDriver(ride.getRideType());
        if (driver == null) {
            System.out.println("No available drivers for " + ride.getRideType().getDescription());
            notifyObservers(ride, "No available drivers at this moment. Please try again.");
            return false;
        }

        ride.setDriver(driver);
        driverService.updateDriverAvailability(driver.getDriverId(), false);
        
        // Calculate fare and store it on the ride
        double fare = fareService.calculateFare(ride, distance);
        ride.setFare(fare);
        
        System.out.println("\n=== Driver Assigned ===");
        System.out.println("Driver: " + driver.getName());
        System.out.println("Vehicle: " + driver.getVehicleNumber());
        System.out.println("Rating: " + driver.getRating());
        
        ride.setStatus(RideStatus.EN_ROUTE);
        notifyObservers(ride, "Driver " + driver.getName() + " has been assigned to your ride and is on the way!");
        
        return true;
    }

    public void startRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.getDriver() != null) {
            ride.setStatus(RideStatus.EN_ROUTE);
            System.out.println("\n=== Ride Started ===");
            System.out.println("Ride ID: " + rideId + " is now en route.");
            notifyObservers(ride, "Your driver is on the way!");
        }
    }

    public void completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.getDriver() != null) {
            ride.setStatus(RideStatus.COMPLETED);
            ride.setCompletedTime(System.currentTimeMillis());
            
            // Deduct fare from passenger
            ride.getPassenger().deductFare(ride.getFare());
            
            // Add earnings to driver
            ride.getDriver().addEarnings(ride.getFare() * 0.8); // Driver gets 80%
            
            // Mark driver as available
            driverService.updateDriverAvailability(ride.getDriver().getDriverId(), true);
            
            System.out.println("\n=== Ride Completed ===");
            System.out.println("Ride ID: " + rideId);
            System.out.println("Fare: $" + String.format("%.2f", ride.getFare()));
            System.out.println("Passenger Wallet: $" + String.format("%.2f", ride.getPassenger().getWalletBalance()));
            
            notifyObservers(ride, "Your ride has been completed. Thank you for using our service!");
        }
    }

    public boolean cancelRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null || ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            return false;
        }

        ride.setStatus(RideStatus.CANCELLED);
        if (ride.getDriver() != null) {
            driverService.updateDriverAvailability(ride.getDriver().getDriverId(), true);
        }

        System.out.println("\n=== Ride Cancelled ===");
        System.out.println("Ride ID: " + rideId);
        notifyObservers(ride, "Your ride has been cancelled.");
        return true;
    }

    public Ride getRide(String rideId) {
        return rides.get(rideId);
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rides.values());
    }

    public int getTotalRides() {
        return rides.size();
    }

    public List<Ride> getRidesByPassenger(String passengerId) {
        List<Ride> rideHistory = new ArrayList<>();
        for (Ride ride : rides.values()) {
            if (ride.getPassenger().getPassengerId().equals(passengerId)) {
                rideHistory.add(ride);
            }
        }
        return rideHistory;
    }
}
