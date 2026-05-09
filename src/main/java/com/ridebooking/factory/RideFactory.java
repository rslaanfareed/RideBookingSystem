package com.ridebooking.factory;

import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;
import com.ridebooking.model.RideType;

public class RideFactory {
    private static int rideCounter = 1000;

    public static Ride createRide(Passenger passenger, String pickupLocation,
                                  String dropoffLocation, RideType rideType) {
        String rideId = "RIDE_" + (++rideCounter);
        return new Ride(rideId, passenger, pickupLocation, dropoffLocation, rideType);
    }

    public static void resetCounter() {
        rideCounter = 1000;
    }
}
