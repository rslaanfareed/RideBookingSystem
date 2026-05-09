package com.ridebooking.service;

import com.ridebooking.model.Passenger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PassengerManagementService {
    private Map<String, Passenger> passengers;
    private static int passengerCounter = 1000;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_PATTERN = "^\\+?\\d{10,15}$";

    public PassengerManagementService() {
        this.passengers = new HashMap<>();
    }

    public Passenger registerPassenger(String name, String email, String phone, String password) {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        String cleanPhone = phone == null ? "" : phone.trim();
        String validationError = validatePassengerInput(cleanName, cleanEmail, cleanPhone, password);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        if (isEmailRegistered(cleanEmail)) {
            throw new IllegalArgumentException("This email is already registered.");
        }

        String passengerId = "PASS_" + (++passengerCounter);
        Passenger passenger = new Passenger(passengerId, cleanName, cleanEmail, cleanPhone, hashPassword(password));
        passengers.put(passengerId, passenger);
        return passenger;
    }

    public String validatePassengerInput(String name, String email, String phone) {
        return validatePassengerInput(name, email, phone, "Password123");
    }

    public String validatePassengerInput(String name, String email, String phone, String password) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required.";
        }
        if (!name.trim().matches("^[A-Za-z ]{2,50}$")) {
            return "Name should contain only letters and spaces, 2 to 50 characters.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.trim().matches(EMAIL_PATTERN)) {
            return "Please enter a valid email address, for example arlan@gmail.com.";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required.";
        }
        if (!phone.trim().matches(PHONE_PATTERN)) {
            return "Phone number must contain only digits, 10 to 15 digits, with optional + at the start.";
        }
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return passwordError;
        }
        return null;
    }

    public String validateLoginInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.trim().matches(EMAIL_PATTERN)) {
            return "Please enter a valid email address.";
        }
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }
        return null;
    }

    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return "Password must include at least one letter and one number.";
        }
        return null;
    }

    public Passenger loginPassenger(String email, String password) {
        String validationError = validateLoginInput(email, password);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        Passenger passenger = getPassengerByEmail(email.trim().toLowerCase());
        if (passenger == null || !passenger.getPasswordHash().equals(hashPassword(password))) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return passenger;
    }

    private boolean isEmailRegistered(String email) {
        return getPassengerByEmail(email) != null;
    }

    private Passenger getPassengerByEmail(String email) {
        for (Passenger passenger : passengers.values()) {
            if (passenger.getEmail().equalsIgnoreCase(email)) {
                return passenger;
            }
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                builder.append(String.format("%02x", hashByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Password hashing is unavailable.", e);
        }
    }

    public Passenger getPassenger(String passengerId) {
        return passengers.get(passengerId);
    }

    public int getTotalPassengers() {
        return passengers.size();
    }

    public List<Passenger> getAllPassengers() {
        return new ArrayList<>(passengers.values());
    }

    public void updatePassengerFunds(String passengerId, double amount) {
        Passenger passenger = passengers.get(passengerId);
        if (passenger != null) {
            passenger.addFunds(amount);
        }
    }
}
