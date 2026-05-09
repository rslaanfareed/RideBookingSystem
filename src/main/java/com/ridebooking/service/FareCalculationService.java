package com.ridebooking.service;

import com.ridebooking.model.Ride;
import com.ridebooking.strategy.FareCalculationStrategy;
import com.ridebooking.strategy.StandardFareStrategy;

public class FareCalculationService {
    private FareCalculationStrategy strategy;

    public FareCalculationService() {
        this.strategy = new StandardFareStrategy();
    }

    public void setFareStrategy(FareCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFare(Ride ride, double distance) {
        double fare = strategy.calculateFare(ride.getRideType().getBaseRate(), distance);
        ride.setFare(fare);
        System.out.println("Fare calculated using " + strategy.getStrategyName() + 
                          ": $" + String.format("%.2f", fare));
        return fare;
    }

    public String getCurrentStrategy() {
        return strategy.getStrategyName();
    }
}
