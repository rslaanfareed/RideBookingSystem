package com.ridebooking.factory;

import com.ridebooking.model.Driver;
import com.ridebooking.model.RideType;

public class DriverFactory {
    private static int driverCounter = 5000;

    public static Driver createStandardDriver(String name, String email, String phone, String vehicleNumber) {
        String driverId = "DRV_STD_" + (++driverCounter);
        return new Driver(driverId, name, email, phone, vehicleNumber, RideType.STANDARD);
    }

    public static Driver createPremiumDriver(String name, String email, String phone, String vehicleNumber) {
        String driverId = "DRV_PRM_" + (++driverCounter);
        return new Driver(driverId, name, email, phone, vehicleNumber, RideType.PREMIUM);
    }

    public static void resetCounter() {
        driverCounter = 5000;
    }
}
