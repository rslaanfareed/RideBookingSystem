package com.ridebooking.strategy;

public class SurgePricingStrategy implements FareCalculationStrategy {
    private static final double SURGE_MULTIPLIER = 1.5;
    private static final double DISTANCE_MULTIPLIER = 12.0;

    @Override
    public double calculateFare(double baseRate, double distance) {
        double baseFare = baseRate * SURGE_MULTIPLIER;
        return baseFare + (distance * DISTANCE_MULTIPLIER);
    }

    @Override
    public String getStrategyName() {
        return "Surge Pricing (1.5x)";
    }
}
