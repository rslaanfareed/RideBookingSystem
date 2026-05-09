package com.ridebooking.service;

import com.ridebooking.model.Driver;
import com.ridebooking.model.RideType;
import java.util.*;

public class DriverManagementService {
    private Map<String, Driver> drivers;

    public DriverManagementService() {
        this.drivers = new HashMap<>();
    }

    public void addDriver(Driver driver) {
        drivers.put(driver.getDriverId(), driver);
    }

    public List<Driver> getAvailableDrivers(RideType rideType) {
        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable() && driver.getRideType() == rideType) {
                availableDrivers.add(driver);
            }
        }
        return availableDrivers;
    }

    public Driver assignDriver(RideType rideType) {
        List<Driver> availableDrivers = getAvailableDrivers(rideType);
        if (availableDrivers.isEmpty()) {
            return null;
        }

        Driver bestDriver = availableDrivers.get(0);
        for (Driver driver : availableDrivers) {
            if (driver.getRating() > bestDriver.getRating()) {
                bestDriver = driver;
            }
        }

        return bestDriver;
    }

    public Driver getDriver(String driverId) {
        return drivers.get(driverId);
    }

    public void updateDriverAvailability(String driverId, boolean available) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.setAvailable(available);
        }
    }

    public int getTotalDrivers() {
        return drivers.size();
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(drivers.values());
    }
}
