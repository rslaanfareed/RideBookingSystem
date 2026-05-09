package com.ridebooking.ui;

import com.ridebooking.model.*;
import com.ridebooking.service.*;
import com.ridebooking.observer.PassengerObserver;
import com.ridebooking.strategy.SurgePricingStrategy;
import com.ridebooking.strategy.StandardFareStrategy;
import java.util.Scanner;
import java.util.List;

public class ConsoleUI {
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private Scanner scanner;

    public ConsoleUI(PassengerManagementService passengerService,
                    DriverManagementService driverService,
                    RideBookingService rideService,
                    FareCalculationService fareService) {
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.fareService = fareService;
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n========================================");
            System.out.println("   ONLINE RIDE BOOKING SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Register Passenger");
            System.out.println("2. Request a Ride");
            System.out.println("3. View Ride Status");
            System.out.println("4. Complete Ride");
            System.out.println("5. Switch Pricing Strategy");
            System.out.println("6. View System Statistics");
            System.out.println("7. Exit");
            System.out.println("========================================");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerPassenger();
                    break;
                case 2:
                    requestRide();
                    break;
                case 3:
                    viewRideStatus();
                    break;
                case 4:
                    completeRide();
                    break;
                case 5:
                    switchPricingStrategy();
                    break;
                case 6:
                    displaySystemStatistics();
                    break;
                case 7:
                    running = false;
                    System.out.println("\nThank you for using Online Ride Booking System!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void registerPassenger() {
        System.out.println("\n=== Register New Passenger ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Create password: ");
        String password = scanner.nextLine();

        Passenger passenger;
        try {
            passenger = passengerService.registerPassenger(name, email, phone, password);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return;
        }
        System.out.println("\nPassenger registered successfully!");
        System.out.println("Passenger ID: " + passenger.getPassengerId());
        System.out.println("Name: " + passenger.getName());
        System.out.println("Initial Wallet Balance: $" + passenger.getWalletBalance());
    }

    private void requestRide() {
        System.out.println("\n=== Request a Ride ===");
        
        if (passengerService.getTotalPassengers() == 0) {
            System.out.println("No passengers registered. Please register first.");
            return;
        }

        System.out.print("Enter Passenger ID: ");
        String passengerId = scanner.nextLine();
        Passenger passenger = passengerService.getPassenger(passengerId);

        if (passenger == null) {
            System.out.println("Passenger not found.");
            return;
        }

        System.out.print("Enter pickup location: ");
        String pickup = scanner.nextLine();
        System.out.print("Enter dropoff location: ");
        String dropoff = scanner.nextLine();

        System.out.println("\nSelect Ride Type:");
        System.out.println("1. Standard ($" + RideType.STANDARD.getBaseRate() + ")");
        System.out.println("2. Premium ($" + RideType.PREMIUM.getBaseRate() + ")");
        System.out.print("Choice: ");
        int rideChoice = getIntInput();
        RideType rideType = rideChoice == 2 ? RideType.PREMIUM : RideType.STANDARD;

        Ride ride = rideService.requestRide(passenger, pickup, dropoff, rideType);
        
        // Register passenger as observer
        PassengerObserver observer = new PassengerObserver(passenger);
        rideService.registerObserver(observer);

        // Simulate distance input
        System.out.print("\nEnter estimated distance (km): ");
        double distance = getDoubleInput();
        scanner.nextLine();

        if (rideService.assignDriverToRide(ride.getRideId(), distance)) {
            rideService.startRide(ride.getRideId());
        }
    }

    private void viewRideStatus() {
        System.out.println("\n=== View Ride Status ===");
        System.out.print("Enter Ride ID: ");
        String rideId = scanner.nextLine();
        
        Ride ride = rideService.getRide(rideId);
        if (ride == null) {
            System.out.println("Ride not found.");
            return;
        }

        System.out.println("\n--- Ride Details ---");
        System.out.println("Ride ID: " + ride.getRideId());
        System.out.println("Passenger: " + ride.getPassenger().getName());
        System.out.println("Pickup: " + ride.getPickupLocation());
        System.out.println("Dropoff: " + ride.getDropoffLocation());
        System.out.println("Ride Type: " + ride.getRideType().getDescription());
        System.out.println("Status: " + ride.getStatus().getDescription());
        if (ride.getDriver() != null) {
            System.out.println("Driver: " + ride.getDriver().getName() + 
                              " (" + ride.getDriver().getVehicleNumber() + ")");
        }
        System.out.println("Fare: $" + String.format("%.2f", ride.getFare()));
    }

    private void completeRide() {
        System.out.println("\n=== Complete Ride ===");
        System.out.print("Enter Ride ID: ");
        String rideId = scanner.nextLine();
        
        Ride ride = rideService.getRide(rideId);
        if (ride == null) {
            System.out.println("Ride not found.");
            return;
        }

        if (ride.getStatus() != RideStatus.EN_ROUTE) {
            System.out.println("Ride is not in EN_ROUTE status.");
            return;
        }

        rideService.completeRide(rideId);
    }

    private void switchPricingStrategy() {
        System.out.println("\n=== Switch Pricing Strategy ===");
        System.out.println("Current Strategy: " + fareService.getCurrentStrategy());
        System.out.println("\n1. Standard Pricing");
        System.out.println("2. Surge Pricing (1.5x)");
        System.out.print("Select strategy: ");
        
        int choice = getIntInput();
        scanner.nextLine();

        if (choice == 1) {
            fareService.setFareStrategy(new StandardFareStrategy());
            System.out.println("Switched to Standard Pricing");
        } else if (choice == 2) {
            fareService.setFareStrategy(new SurgePricingStrategy());
            System.out.println("Switched to Surge Pricing");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void displaySystemStatistics() {
        System.out.println("\n========== SYSTEM STATISTICS ==========");
        System.out.println("Total Passengers: " + passengerService.getTotalPassengers());
        System.out.println("Total Drivers: " + driverService.getTotalDrivers());
        System.out.println("Total Rides: " + rideService.getTotalRides());
        System.out.println("\n--- Drivers ---");
        List<Driver> drivers = driverService.getAllDrivers();
        for (Driver driver : drivers) {
            System.out.println("  " + driver.getName() + " (" + driver.getDriverId() + ") - " +
                              driver.getRideType().getDescription() + " - " +
                              (driver.isAvailable() ? "Available" : "Busy"));
        }
        System.out.println("\n--- Recent Rides ---");
        List<Ride> rides = rideService.getAllRides();
        for (Ride ride : rides) {
            System.out.println("  " + ride.getRideId() + ": " + 
                              ride.getPassenger().getName() + " - " +
                              ride.getStatus().getDescription() + " - $" +
                              String.format("%.2f", ride.getFare()));
        }
        System.out.println("=====================================");
    }

    private int getIntInput() {
        try {
            return scanner.nextInt();
        } catch (NumberFormatException e) {
            scanner.nextLine();
            return -1;
        }
    }

    private double getDoubleInput() {
        try {
            return scanner.nextDouble();
        } catch (NumberFormatException e) {
            scanner.nextLine();
            return -1;
        }
    }
}
