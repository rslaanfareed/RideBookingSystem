package com.ridebooking.observer;

import com.ridebooking.model.Ride;

public interface RideStatusObserver {
    void update(Ride ride, String message);
}
