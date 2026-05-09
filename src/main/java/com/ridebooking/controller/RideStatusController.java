package com.ridebooking.controller;

import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;
import com.ridebooking.model.RideStatus;
import com.ridebooking.service.DriverManagementService;
import com.ridebooking.service.FareCalculationService;
import com.ridebooking.service.PassengerManagementService;
import com.ridebooking.service.RideBookingService;
import com.ridebooking.ui.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RideStatusController {
    private Passenger currentPassenger;
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private JFrame mainFrame;

    public RideStatusController(Passenger currentPassenger,
                                PassengerManagementService passengerService,
                                DriverManagementService driverService,
                                RideBookingService rideService,
                                FareCalculationService fareService,
                                JFrame mainFrame) {
        this.currentPassenger = currentPassenger;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.fareService = fareService;
        this.mainFrame = mainFrame;
    }

    public JPanel createPanel() {
        JPanel mainPanel = AppTheme.page();
        mainPanel.add(AppTheme.header("Ride Status", "Search active rides and cancel when plans change"), BorderLayout.NORTH);

        JButton backBtn = AppTheme.secondaryButton("< Back to Dashboard");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(AppTheme.BACKGROUND);
        topPanel.add(backBtn);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(AppTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel statusLabel = AppTheme.title("Find your ride", 22);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField rideIdField = new JTextField(30);
        AppTheme.styleField(rideIdField);
        rideIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea statusArea = AppTheme.readOnlyArea(10, 50);
        statusArea.setText("Status information will appear here...");

        JButton searchBtn = AppTheme.secondaryButton("Search Ride");
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton completeBtn = AppTheme.primaryButton("Complete Ride");
        completeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeBtn.setEnabled(false);

        JButton cancelBtn = AppTheme.dangerButton("Cancel Ride");
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.setEnabled(false);

        String[] historyColumns = {"Ride ID", "Pickup", "Dropoff", "Driver", "Status", "Fare"};
        DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.setPreferredScrollableViewportSize(new Dimension(760, 170));

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Your Ride History"));
        updateHistory(historyModel);

        searchBtn.addActionListener(e -> {
            Ride ride = findPassengerRide(rideIdField.getText().trim());
            if (ride == null) {
                JOptionPane.showMessageDialog(mainFrame, "Ride not found for this passenger", "Error", JOptionPane.ERROR_MESSAGE);
                completeBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                return;
            }

            statusArea.setText(formatRideInfo(ride));
            completeBtn.setEnabled(canComplete(ride));
            cancelBtn.setEnabled(canCancel(ride));
        });

        completeBtn.addActionListener(e -> {
            Ride ride = findPassengerRide(rideIdField.getText().trim());
            if (ride == null) {
                JOptionPane.showMessageDialog(mainFrame, "Ride not found for this passenger", "Error", JOptionPane.ERROR_MESSAGE);
                completeBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                return;
            }

            if (!canComplete(ride)) {
                JOptionPane.showMessageDialog(mainFrame, "Only en route rides can be completed.", "Complete Ride", JOptionPane.WARNING_MESSAGE);
                completeBtn.setEnabled(false);
                return;
            }

            rideService.completeRide(ride.getRideId());
            Ride updatedRide = rideService.getRide(ride.getRideId());
            statusArea.setText(formatRideInfo(updatedRide));
            completeBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            updateHistory(historyModel);

            if (updatedRide.getDriver() != null) {
                String ratingStr = JOptionPane.showInputDialog(mainFrame,
                        "Rate driver " + updatedRide.getDriver().getName() + " from 1 to 5:",
                        "Rate Driver", JOptionPane.QUESTION_MESSAGE);
                saveDriverRating(updatedRide, ratingStr);
            }
        });

        cancelBtn.addActionListener(e -> {
            Ride ride = findPassengerRide(rideIdField.getText().trim());
            if (ride == null) {
                JOptionPane.showMessageDialog(mainFrame, "Ride not found for this passenger", "Error", JOptionPane.ERROR_MESSAGE);
                completeBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                return;
            }

            if (!canCancel(ride)) {
                JOptionPane.showMessageDialog(mainFrame, "This ride cannot be cancelled now.", "Cancel Ride", JOptionPane.WARNING_MESSAGE);
                cancelBtn.setEnabled(false);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Cancel ride " + ride.getRideId() + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            if (rideService.cancelRide(ride.getRideId())) {
                Ride updatedRide = rideService.getRide(ride.getRideId());
                statusArea.setText(formatRideInfo(updatedRide));
                completeBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                updateHistory(historyModel);
                JOptionPane.showMessageDialog(mainFrame, "Ride cancelled successfully.", "Ride Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        center.add(statusLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(AppTheme.body("Ride ID"));
        center.add(Box.createVerticalStrut(6));
        center.add(rideIdField);
        center.add(Box.createVerticalStrut(18));
        center.add(searchBtn);
        center.add(Box.createVerticalStrut(10));
        center.add(completeBtn);
        center.add(Box.createVerticalStrut(10));
        center.add(cancelBtn);
        center.add(Box.createVerticalStrut(20));
        center.add(new JScrollPane(statusArea));
        center.add(Box.createVerticalStrut(20));
        center.add(historyScroll);

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

    private Ride findPassengerRide(String rideId) {
        if (rideId.isEmpty()) {
            return null;
        }
        Ride ride = rideService.getRide(rideId);
        if (ride == null || !ride.getPassenger().getPassengerId().equals(currentPassenger.getPassengerId())) {
            return null;
        }
        return ride;
    }

    private boolean canCancel(Ride ride) {
        return ride.getStatus() != RideStatus.COMPLETED && ride.getStatus() != RideStatus.CANCELLED;
    }

    private boolean canComplete(Ride ride) {
        return ride.getStatus() == RideStatus.EN_ROUTE;
    }

    private void saveDriverRating(Ride ride, String ratingStr) {
        if (ratingStr == null || ratingStr.trim().isEmpty()) {
            return;
        }
        try {
            double ratingValue = Double.parseDouble(ratingStr.trim());
            if (ratingValue >= 1.0 && ratingValue <= 5.0) {
                ride.getDriver().addRating(ratingValue);
                JOptionPane.showMessageDialog(mainFrame,
                        "Thank you! Driver rating updated to " + String.format("%.1f", ride.getDriver().getRating()),
                        "Rating Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please enter a rating between 1 and 5.", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Rating must be a number.", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String formatRideInfo(Ride ride) {
        return "Ride ID: " + ride.getRideId() + "\n" +
                "Pickup: " + ride.getPickupLocation() + "\n" +
                "Dropoff: " + ride.getDropoffLocation() + "\n" +
                "Status: " + ride.getStatus().getDescription() + "\n" +
                "Driver: " + (ride.getDriver() != null
                ? ride.getDriver().getName() + " (" + ride.getDriver().getVehicleNumber() + ")"
                : "Not assigned") + "\n" +
                "Fare: $" + String.format("%.2f", ride.getFare()) + "\n" +
                "Ride Type: " + ride.getRideType().getDescription();
    }

    private void updateHistory(DefaultTableModel historyModel) {
        historyModel.setRowCount(0);
        for (Ride historyRide : rideService.getRidesByPassenger(currentPassenger.getPassengerId())) {
            historyModel.addRow(new Object[]{
                    historyRide.getRideId(),
                    historyRide.getPickupLocation(),
                    historyRide.getDropoffLocation(),
                    historyRide.getDriver() != null ? historyRide.getDriver().getName() : "Pending",
                    historyRide.getStatus().getDescription(),
                    String.format("$%.2f", historyRide.getFare())
            });
        }
    }
}
