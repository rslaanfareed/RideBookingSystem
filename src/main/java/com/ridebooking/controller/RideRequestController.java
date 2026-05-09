package com.ridebooking.controller;

import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;
import com.ridebooking.model.RideStatus;
import com.ridebooking.model.RideType;
import com.ridebooking.service.DriverManagementService;
import com.ridebooking.service.FareCalculationService;
import com.ridebooking.service.PassengerManagementService;
import com.ridebooking.service.RideBookingService;
import com.ridebooking.ui.AppTheme;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class RideRequestController {
    private Passenger currentPassenger;
    private PassengerManagementService passengerService;
    private RideBookingService rideService;
    private DriverManagementService driverService;
    private FareCalculationService fareService;
    private JFrame mainFrame;
    private Ride currentRide;
    private double currentDistance;

    public RideRequestController(Passenger passenger,
                                 PassengerManagementService passengerService,
                                 RideBookingService rideService,
                                 DriverManagementService driverService,
                                 FareCalculationService fareService,
                                 JFrame mainFrame) {
        this.currentPassenger = passenger;
        this.passengerService = passengerService;
        this.rideService = rideService;
        this.driverService = driverService;
        this.fareService = fareService;
        this.mainFrame = mainFrame;
    }

    public JPanel createPanel() {
        JPanel mainPanel = AppTheme.page();
        mainPanel.add(AppTheme.header("Request Ride", "Preview fare, match a driver, and manage the trip"), BorderLayout.NORTH);

        JButton backBtn = AppTheme.secondaryButton("< Back to Dashboard");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(AppTheme.BACKGROUND);
        topPanel.add(backBtn);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(AppTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(28, 52, 28, 52));

        JPanel formCard = AppTheme.card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(560, 520));

        JLabel formLabel = AppTheme.title("Where are you going?", 22);
        formLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel balanceLabel = AppTheme.body("Wallet balance: $" + String.format("%.2f", currentPassenger.getWalletBalance()));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField pickupField = new JTextField(30);
        JTextField dropoffField = new JTextField(30);
        AppTheme.styleField(pickupField);
        AppTheme.styleField(dropoffField);

        JPanel shortcutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        shortcutPanel.setOpaque(false);
        JButton currentLocationBtn = AppTheme.secondaryButton("Use Current");
        JButton universityBtn = AppTheme.secondaryButton("UET");
        JButton airportBtn = AppTheme.secondaryButton("Airport");
        currentLocationBtn.addActionListener(e -> pickupField.setText("Current Location"));
        universityBtn.addActionListener(e -> dropoffField.setText("UET Taxila"));
        airportBtn.addActionListener(e -> dropoffField.setText("Islamabad Airport"));
        shortcutPanel.add(currentLocationBtn);
        shortcutPanel.add(universityBtn);
        shortcutPanel.add(airportBtn);

        JSpinner distanceSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.5, 100.0, 0.5));
        distanceSpinner.setMaximumSize(new Dimension(420, 38));

        JComboBox<String> rideTypeCombo = new JComboBox<>();
        rideTypeCombo.addItem("Standard Ride ($" + RideType.STANDARD.getBaseRate() + ")");
        rideTypeCombo.addItem("Premium Ride ($" + RideType.PREMIUM.getBaseRate() + ")");
        rideTypeCombo.setMaximumSize(new Dimension(420, 38));

        JLabel farePreview = AppTheme.title("", 16);
        farePreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        Runnable updatePreview = () -> {
            RideType rideType = getSelectedRideType(rideTypeCombo);
            double distance = (Double) distanceSpinner.getValue();
            farePreview.setText("Estimated fare: $" + String.format("%.2f", estimateFare(rideType, distance))
                    + " | ETA: " + estimateEta(distance) + " min");
        };
        ChangeListener previewChange = e -> updatePreview.run();
        distanceSpinner.addChangeListener(previewChange);
        rideTypeCombo.addActionListener(e -> updatePreview.run());
        updatePreview.run();

        JButton requestBtn = AppTheme.primaryButton("Confirm Booking");
        requestBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rideStatusPanel = AppTheme.card();
        rideStatusPanel.setLayout(new BoxLayout(rideStatusPanel, BoxLayout.Y_AXIS));
        rideStatusPanel.setVisible(false);
        rideStatusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rideStatusPanel.setMaximumSize(new Dimension(640, 430));

        JLabel activeTitle = AppTheme.title("Active ride", 20);
        activeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea rideDetailsArea = AppTheme.readOnlyArea(9, 48);

        JButton refreshBtn = AppTheme.secondaryButton("Refresh Status");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton completeBtn = AppTheme.primaryButton("Complete Ride");
        completeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeBtn.setEnabled(false);
        JButton cancelBtn = AppTheme.dangerButton("Cancel Ride");
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.setEnabled(false);

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        ratingPanel.setBackground(AppTheme.SURFACE);
        ratingPanel.setVisible(false);
        JLabel ratingLabel = new JLabel("Rate driver:");
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 5.0, 0.5));
        JButton submitRatingBtn = AppTheme.button("Submit Rating", AppTheme.ACCENT, AppTheme.TEXT);
        ratingPanel.add(ratingLabel);
        ratingPanel.add(ratingSpinner);
        ratingPanel.add(submitRatingBtn);

        rideStatusPanel.add(activeTitle);
        rideStatusPanel.add(Box.createVerticalStrut(12));
        rideStatusPanel.add(new JScrollPane(rideDetailsArea));
        rideStatusPanel.add(Box.createVerticalStrut(12));
        rideStatusPanel.add(refreshBtn);
        rideStatusPanel.add(Box.createVerticalStrut(8));
        rideStatusPanel.add(completeBtn);
        rideStatusPanel.add(Box.createVerticalStrut(8));
        rideStatusPanel.add(cancelBtn);
        rideStatusPanel.add(Box.createVerticalStrut(8));
        rideStatusPanel.add(ratingPanel);

        requestBtn.addActionListener(e -> {
            String pickup = pickupField.getText().trim();
            String dropoff = dropoffField.getText().trim();

            if (pickup.isEmpty() || dropoff.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter pickup and dropoff locations", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RideType rideType = getSelectedRideType(rideTypeCombo);
            double distance = (Double) distanceSpinner.getValue();
            double estimatedFare = estimateFare(rideType, distance);

            if (currentPassenger.getWalletBalance() < estimatedFare) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Your wallet balance is lower than the estimated fare.\nPlease add funds in Settings.",
                        "Low Wallet Balance", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentRide = rideService.requestRide(currentPassenger, pickup, dropoff, rideType);
            boolean assigned = rideService.assignDriverToRide(currentRide.getRideId(), distance);

            if (!assigned) {
                JOptionPane.showMessageDialog(mainFrame, "No available driver currently. Please try again later.", "No Driver Available", JOptionPane.WARNING_MESSAGE);
                currentRide = null;
                return;
            }

            currentDistance = distance;
            formCard.setVisible(false);
            rideStatusPanel.setVisible(true);
            completeBtn.setEnabled(true);
            cancelBtn.setEnabled(true);
            ratingPanel.setVisible(false);
            updateRideDetailsArea(rideDetailsArea);
            center.revalidate();
            center.repaint();
        });

        refreshBtn.addActionListener(e -> {
            if (currentRide != null) {
                currentRide = rideService.getRide(currentRide.getRideId());
                updateRideDetailsArea(rideDetailsArea);
                completeBtn.setEnabled(currentRide.getStatus() == RideStatus.EN_ROUTE);
                cancelBtn.setEnabled(canCancel(currentRide));
            }
        });

        completeBtn.addActionListener(e -> {
            if (currentRide == null) {
                return;
            }
            if (currentRide.getStatus() != RideStatus.EN_ROUTE) {
                JOptionPane.showMessageDialog(mainFrame, "Ride is not currently en route", "Complete Ride", JOptionPane.WARNING_MESSAGE);
                return;
            }

            rideService.completeRide(currentRide.getRideId());
            currentRide = rideService.getRide(currentRide.getRideId());
            updateRideDetailsArea(rideDetailsArea);
            completeBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            ratingPanel.setVisible(true);
        });

        cancelBtn.addActionListener(e -> {
            if (currentRide == null) {
                return;
            }
            if (!canCancel(currentRide)) {
                JOptionPane.showMessageDialog(mainFrame, "This ride cannot be cancelled now.", "Cancel Ride", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Cancel ride " + currentRide.getRideId() + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            if (rideService.cancelRide(currentRide.getRideId())) {
                currentRide = rideService.getRide(currentRide.getRideId());
                updateRideDetailsArea(rideDetailsArea);
                completeBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                ratingPanel.setVisible(false);
                JOptionPane.showMessageDialog(mainFrame, "Ride cancelled successfully.", "Ride Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        submitRatingBtn.addActionListener(e -> {
            if (currentRide == null || currentRide.getDriver() == null) {
                return;
            }
            double rating = (Double) ratingSpinner.getValue();
            currentRide.getDriver().addRating(rating);
            JOptionPane.showMessageDialog(mainFrame,
                    "Thank you! Driver rating updated to " + String.format("%.1f", currentRide.getDriver().getRating()),
                    "Rating Saved", JOptionPane.INFORMATION_MESSAGE);
            ratingPanel.setVisible(false);
        });

        formCard.add(formLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(balanceLabel);
        formCard.add(Box.createVerticalStrut(20));
        addLabeledField(formCard, "Pickup location", pickupField);
        formCard.add(Box.createVerticalStrut(8));
        addLabeledField(formCard, "Dropoff location", dropoffField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(shortcutPanel);
        formCard.add(Box.createVerticalStrut(14));
        addLabeledField(formCard, "Distance (km)", distanceSpinner);
        formCard.add(Box.createVerticalStrut(14));
        addLabeledField(formCard, "Ride type", rideTypeCombo);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(farePreview);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(requestBtn);

        center.add(formCard);
        center.add(Box.createVerticalStrut(18));
        center.add(rideStatusPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(topPanel, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(center), BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            DashboardController dashboard = new DashboardController(
                    currentPassenger, passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(dashboard.createPanel());
            mainFrame.revalidate();
        });

        mainPanel.add(wrapper, BorderLayout.CENTER);
        return mainPanel;
    }

    private void updateRideDetailsArea(JTextArea area) {
        if (currentRide == null) {
            return;
        }

        String details = "Ride ID: " + currentRide.getRideId() + "\n" +
                "Pickup: " + currentRide.getPickupLocation() + "\n" +
                "Dropoff: " + currentRide.getDropoffLocation() + "\n" +
                "Status: " + currentRide.getStatus().getDescription() + "\n" +
                "Driver: " + (currentRide.getDriver() != null
                ? currentRide.getDriver().getName() + " (" + currentRide.getDriver().getVehicleNumber() + ")"
                : "Not assigned") + "\n" +
                "Fare: $" + String.format("%.2f", currentRide.getFare()) + "\n" +
                "Estimated ETA: " + estimateEta(currentDistance) + " min\n" +
                "Ride Type: " + currentRide.getRideType().getDescription();

        if (currentRide.getDriver() != null) {
            details += "\nDriver Rating: " + String.format("%.1f", currentRide.getDriver().getRating()) + "/5.0";
        }

        area.setText(details);
    }

    private void addLabeledField(JPanel parent, String labelText, JComponent component) {
        JLabel label = AppTheme.body(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        parent.add(component);
    }

    private RideType getSelectedRideType(JComboBox<String> rideTypeCombo) {
        return rideTypeCombo.getSelectedIndex() == 1 ? RideType.PREMIUM : RideType.STANDARD;
    }

    private boolean canCancel(Ride ride) {
        return ride.getStatus() != RideStatus.COMPLETED && ride.getStatus() != RideStatus.CANCELLED;
    }

    private double estimateFare(RideType rideType, double distance) {
        if (fareService.getCurrentStrategy().toLowerCase().contains("surge")) {
            return (rideType.getBaseRate() * 1.5) + (distance * 12.0);
        }
        return rideType.getBaseRate() + (distance * 10.0);
    }

    private int estimateEta(double distance) {
        return Math.max(4, (int) Math.ceil(distance * 3.0));
    }
}
