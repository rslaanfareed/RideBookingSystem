package com.ridebooking.controller;

import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;
import com.ridebooking.model.RideStatus;
import com.ridebooking.service.*;
import com.ridebooking.ui.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardController {
    private Passenger currentPassenger;
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private JFrame mainFrame;

    public DashboardController(Passenger passenger,
                               PassengerManagementService passengerService,
                               DriverManagementService driverService,
                               RideBookingService rideService,
                               FareCalculationService fareService,
                               JFrame mainFrame) {
        this.currentPassenger = passenger;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.fareService = fareService;
        this.mainFrame = mainFrame;
    }

    public JPanel createPanel() {
        JPanel mainPanel = AppTheme.page();

        JPanel header = AppTheme.header("Welcome, " + currentPassenger.getName(),
                "Wallet $" + String.format("%.2f", currentPassenger.getWalletBalance()) + " | " + fareService.getCurrentStrategy());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppTheme.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 14, 22, 14));

        JLabel navTitle = new JLabel("Menu");
        navTitle.setFont(AppTheme.font(16, Font.BOLD));
        navTitle.setForeground(Color.WHITE);

        JButton requestRideBtn = createNavButton("Request Ride");
        JButton viewStatusBtn = createNavButton("View Ride Status");
        JButton settingsBtn = createNavButton("Settings");
        JButton statsBtn = createNavButton("Statistics");
        JButton logoutBtn = createNavButton("Logout");

        requestRideBtn.addActionListener(e -> {
            RideRequestController rideReq = new RideRequestController(
                    currentPassenger, passengerService, rideService, driverService, fareService, mainFrame
            );
            mainFrame.setContentPane(rideReq.createPanel());
            mainFrame.revalidate();
        });

        viewStatusBtn.addActionListener(e -> {
            RideStatusController rideStatus = new RideStatusController(
                    currentPassenger, passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(rideStatus.createPanel());
            mainFrame.revalidate();
        });

        settingsBtn.addActionListener(e -> {
            SettingsController settings = new SettingsController(
                    currentPassenger, passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(settings.createPanel());
            mainFrame.revalidate();
        });

        statsBtn.addActionListener(e -> {
            StatisticsController stats = new StatisticsController(
                    currentPassenger, passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(stats.createPanel());
            mainFrame.revalidate();
        });

        logoutBtn.addActionListener(e -> {
            LoginController login = new LoginController(
                    passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(login.createPanel());
            mainFrame.revalidate();
        });

        sidebar.add(navTitle);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(requestRideBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(viewStatusBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(settingsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(statsBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setBackground(AppTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel dashTitle = AppTheme.title("Your ride dashboard", 26);
        dashTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(dashTitle);
        content.add(Box.createVerticalStrut(18));

        JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 14));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        List<Ride> rides = rideService.getRidesByPassenger(currentPassenger.getPassengerId());
        int activeRides = 0;
        for (Ride ride : rides) {
            if (ride.getStatus() != RideStatus.COMPLETED) {
                activeRides++;
            }
        }
        metrics.add(metricCard("Wallet", "$" + String.format("%.2f", currentPassenger.getWalletBalance()), "Available balance"));
        metrics.add(metricCard("Trips", String.valueOf(rides.size()), "Total bookings"));
        metrics.add(metricCard("Active", String.valueOf(activeRides), "Rides in progress"));
        content.add(metrics);
        content.add(Box.createVerticalStrut(18));

        JPanel profileCard = AppTheme.card();
        profileCard.setLayout(new GridLayout(4, 2, 8, 10));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(AppTheme.body("Passenger ID"));
        profileCard.add(new JLabel(currentPassenger.getPassengerId()));
        profileCard.add(AppTheme.body("Email"));
        profileCard.add(new JLabel(currentPassenger.getEmail()));
        profileCard.add(AppTheme.body("Phone"));
        profileCard.add(new JLabel(currentPassenger.getPhone()));
        profileCard.add(AppTheme.body("Current pricing"));
        profileCard.add(new JLabel(fareService.getCurrentStrategy()));

        JPanel actions = AppTheme.card();
        actions.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton quickBook = AppTheme.primaryButton("Book Ride");
        JButton history = AppTheme.secondaryButton("Ride History");
        JButton wallet = AppTheme.secondaryButton("Wallet");
        quickBook.addActionListener(requestRideBtn.getActionListeners()[0]);
        history.addActionListener(viewStatusBtn.getActionListeners()[0]);
        wallet.addActionListener(settingsBtn.getActionListeners()[0]);
        actions.add(quickBook);
        actions.add(history);
        actions.add(wallet);

        content.add(profileCard);
        content.add(Box.createVerticalStrut(18));
        content.add(actions);
        center.add(content, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(center, BorderLayout.CENTER);

        return mainPanel;
    }

    private JButton createNavButton(String text) {
        JButton btn = AppTheme.secondaryButton(text);
        btn.setMaximumSize(new Dimension(180, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JPanel metricCard(String label, String value, String hint) {
        JPanel card = AppTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel labelView = AppTheme.body(label);
        JLabel valueView = AppTheme.title(value, 24);
        JLabel hintView = AppTheme.body(hint);
        card.add(labelView);
        card.add(Box.createVerticalStrut(6));
        card.add(valueView);
        card.add(Box.createVerticalStrut(4));
        card.add(hintView);
        return card;
    }
}
