package com.ridebooking.strategy;

public class StandardFareStrategy implements FareCalculationStrategy {
    private static final double DISTANCE_MULTIPLIER = 10.0;
    private static final double BASE_MULTIPLIER = 1.0;

    @Override
    public double calculateFare(double baseRate, double distance) {
        return (baseRate * BASE_MULTIPLIER) + (distance * DISTANCE_MULTIPLIER);
    }

    @Override
    public String getStrategyName() {
        return "Standard Pricing";
    }
}
