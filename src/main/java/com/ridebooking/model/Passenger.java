package com.ridebooking.model;

public class Passenger {
    private String passengerId;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private double walletBalance;

    public Passenger(String passengerId, String name, String email, String phone, String passwordHash) {
        this.passengerId = passengerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.walletBalance = 1000.0;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void deductFare(double amount) {
        this.walletBalance -= amount;
    }

    public void addFunds(double amount) {
        this.walletBalance += amount;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId='" + passengerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
