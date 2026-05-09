package com.ridebooking.controller;

import com.ridebooking.model.Passenger;
import com.ridebooking.service.DriverManagementService;
import com.ridebooking.service.FareCalculationService;
import com.ridebooking.service.PassengerManagementService;
import com.ridebooking.service.RideBookingService;
import com.ridebooking.strategy.StandardFareStrategy;
import com.ridebooking.strategy.SurgePricingStrategy;
import com.ridebooking.ui.AppTheme;

import javax.swing.*;
import java.awt.*;

public class SettingsController {
    private Passenger currentPassenger;
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private JFrame mainFrame;

    public SettingsController(Passenger currentPassenger,
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
        mainPanel.add(AppTheme.header("Settings", "Manage pricing mode and wallet balance"), BorderLayout.NORTH);

        JButton backBtn = AppTheme.secondaryButton("< Back to Dashboard");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(AppTheme.BACKGROUND);
        topPanel.add(backBtn);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(AppTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel settingsLabel = AppTheme.title("Ride preferences", 22);
        settingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel currentLabel = new JLabel("Current Strategy: " + fareService.getCurrentStrategy());
        currentLabel.setFont(AppTheme.font(14, Font.PLAIN));
        currentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel strategyBox = AppTheme.card();
        strategyBox.setLayout(new BoxLayout(strategyBox, BoxLayout.Y_AXIS));
        strategyBox.setMaximumSize(new Dimension(540, 320));

        JLabel strategyTitle = AppTheme.title("Select pricing strategy", 16);
        strategyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton standardRadio = new JRadioButton("Standard Pricing (Base + Distance)");
        JRadioButton surgeRadio = new JRadioButton("Surge Pricing (1.5x Base + Distance)");
        standardRadio.setBackground(AppTheme.SURFACE);
        surgeRadio.setBackground(AppTheme.SURFACE);

        ButtonGroup group = new ButtonGroup();
        group.add(standardRadio);
        group.add(surgeRadio);
        standardRadio.setSelected(!fareService.getCurrentStrategy().toLowerCase().contains("surge"));
        surgeRadio.setSelected(fareService.getCurrentStrategy().toLowerCase().contains("surge"));

        JTextArea descriptionArea = AppTheme.readOnlyArea(6, 50);
        descriptionArea.setText("Standard Pricing:\n" +
                "- Base fare + distance multiplier\n" +
                "- Best for regular city rides\n\n" +
                "Surge Pricing:\n" +
                "- Higher multiplier during busy hours\n" +
                "- Useful for demonstrating Strategy pattern behavior");

        JButton applyBtn = AppTheme.primaryButton("Apply Strategy");
        applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyBtn.addActionListener(e -> {
            if (standardRadio.isSelected()) {
                fareService.setFareStrategy(new StandardFareStrategy());
                JOptionPane.showMessageDialog(mainFrame, "Switched to Standard Pricing", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                fareService.setFareStrategy(new SurgePricingStrategy());
                JOptionPane.showMessageDialog(mainFrame, "Switched to Surge Pricing", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            currentLabel.setText("Current Strategy: " + fareService.getCurrentStrategy());
        });

        strategyBox.add(strategyTitle);
        strategyBox.add(Box.createVerticalStrut(10));
        strategyBox.add(standardRadio);
        strategyBox.add(Box.createVerticalStrut(5));
        strategyBox.add(surgeRadio);
        strategyBox.add(Box.createVerticalStrut(15));
        strategyBox.add(new JScrollPane(descriptionArea));
        strategyBox.add(Box.createVerticalStrut(15));
        strategyBox.add(applyBtn);

        JPanel walletBox = AppTheme.card();
        walletBox.setLayout(new BoxLayout(walletBox, BoxLayout.Y_AXIS));
        walletBox.setMaximumSize(new Dimension(540, 190));

        JLabel walletTitle = AppTheme.title("Wallet", 16);
        JLabel walletBalance = AppTheme.body("Current balance: $" + String.format("%.2f", currentPassenger.getWalletBalance()));
        JSpinner topUpSpinner = new JSpinner(new SpinnerNumberModel(500.0, 50.0, 5000.0, 50.0));
        topUpSpinner.setMaximumSize(new Dimension(220, 38));
        JButton addFundsBtn = AppTheme.primaryButton("Add Funds");
        addFundsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFundsBtn.addActionListener(e -> {
            double amount = (Double) topUpSpinner.getValue();
            currentPassenger.addFunds(amount);
            walletBalance.setText("Current balance: $" + String.format("%.2f", currentPassenger.getWalletBalance()));
            JOptionPane.showMessageDialog(mainFrame,
                    "$" + String.format("%.2f", amount) + " added to wallet",
                    "Wallet Updated", JOptionPane.INFORMATION_MESSAGE);
        });

        walletBox.add(walletTitle);
        walletBox.add(Box.createVerticalStrut(8));
        walletBox.add(walletBalance);
        walletBox.add(Box.createVerticalStrut(12));
        walletBox.add(AppTheme.body("Top-up amount"));
        walletBox.add(Box.createVerticalStrut(5));
        walletBox.add(topUpSpinner);
        walletBox.add(Box.createVerticalStrut(12));
        walletBox.add(addFundsBtn);

        center.add(settingsLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(currentLabel);
        center.add(Box.createVerticalStrut(30));
        center.add(strategyBox);
        center.add(Box.createVerticalStrut(18));
        center.add(walletBox);
        center.add(Box.createVerticalGlue());

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
}
