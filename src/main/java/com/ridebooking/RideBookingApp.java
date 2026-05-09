package com.ridebooking;

import com.ridebooking.controller.LoginController;
import com.ridebooking.factory.DriverFactory;
import com.ridebooking.service.*;
import javax.swing.*;

public class RideBookingApp {
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private FareCalculationService fareService;
    private RideBookingService rideService;
    private static JFrame mainFrame;

    public RideBookingApp() {
        // Initialize services
        passengerService = new PassengerManagementService();
        driverService = new DriverManagementService();
        fareService = new FareCalculationService();
        rideService = new RideBookingService(driverService, fareService);

        // Initialize sample drivers
        initializeSampleDrivers();

        // Create main frame
        mainFrame = new JFrame("RideFlow - Smart Ride Booking");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1040, 720);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setMinimumSize(new java.awt.Dimension(960, 640));
        mainFrame.setResizable(true);

        // Show login screen
        LoginController loginController = new LoginController(
            passengerService, driverService, rideService, fareService, mainFrame
        );
        mainFrame.setContentPane(loginController.createPanel());
        mainFrame.setVisible(true);
    }

    private void initializeSampleDrivers() {
        driverService.addDriver(DriverFactory.createStandardDriver(
            "Ahmed Ali", "ahmed@example.com", "03001234567", "ABC-1234"));
        driverService.addDriver(DriverFactory.createStandardDriver(
            "Fatima Khan", "fatima@example.com", "03009876543", "XYZ-5678"));
        driverService.addDriver(DriverFactory.createPremiumDriver(
            "Usman Hassan", "usman@example.com", "03102468135", "PRE-1111"));
        driverService.addDriver(DriverFactory.createPremiumDriver(
            "Aisha Malik", "aisha@example.com", "03115552468", "PRE-2222"));
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RideBookingApp());
    }
}
