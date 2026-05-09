package com.ridebooking.strategy;

public interface FareCalculationStrategy {
    double calculateFare(double baseRate, double distance);
    String getStrategyName();
}
