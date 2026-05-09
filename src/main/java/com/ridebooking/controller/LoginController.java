package com.ridebooking.controller;

import com.ridebooking.model.Passenger;
import com.ridebooking.service.DriverManagementService;
import com.ridebooking.service.FareCalculationService;
import com.ridebooking.service.PassengerManagementService;
import com.ridebooking.service.RideBookingService;
import com.ridebooking.ui.AppTheme;

import javax.swing.*;
import java.awt.*;

public class LoginController {
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private JFrame mainFrame;

    public LoginController(PassengerManagementService passengerService,
                           DriverManagementService driverService,
                           RideBookingService rideService,
                           FareCalculationService fareService,
                           JFrame mainFrame) {
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.fareService = fareService;
        this.mainFrame = mainFrame;
    }

    public JPanel createPanel() {
        JPanel mainPanel = AppTheme.page();
        JPanel header = AppTheme.header("RideFlow", "Sign in to book, track, cancel, and manage rides");

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(AppTheme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        JPanel formCard = AppTheme.card();
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setPreferredSize(new Dimension(500, 540));

        JLabel formTitle = AppTheme.title("Passenger access", 24);
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.font(13, Font.BOLD));
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Sign Up", createSignUpPanel());

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(tabs, BorderLayout.CENTER);
        center.add(formCard);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = formPanel();

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        AppTheme.styleField(emailField);
        AppTheme.styleField(passwordField);

        JButton loginBtn = AppTheme.primaryButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            try {
                Passenger passenger = passengerService.loginPassenger(email, password);
                openDashboard(passenger);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(AppTheme.body("Email"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(AppTheme.body("Password"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(24));
        panel.add(loginBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createSignUpPanel() {
        JPanel panel = formPanel();

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        AppTheme.styleField(nameField);
        AppTheme.styleField(emailField);
        AppTheme.styleField(phoneField);
        AppTheme.styleField(passwordField);
        AppTheme.styleField(confirmPasswordField);

        JButton signUpBtn = AppTheme.primaryButton("Create Account");
        signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signUpBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Passenger passenger = passengerService.registerPassenger(name, email, phone, password);
                JOptionPane.showMessageDialog(mainFrame,
                        "Welcome " + passenger.getName() + "\nPassenger ID: " + passenger.getPassengerId(),
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);
                openDashboard(passenger);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(AppTheme.body("Name"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(AppTheme.body("Email"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(AppTheme.body("Phone"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(AppTheme.body("Password"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(AppTheme.body("Confirm Password"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(AppTheme.body("Password must be 8+ characters and include letters and numbers."));
        panel.add(Box.createVerticalStrut(16));
        panel.add(signUpBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        return panel;
    }

    private void openDashboard(Passenger passenger) {
        DashboardController dashboard = new DashboardController(
                passenger, passengerService, driverService, rideService, fareService, mainFrame
        );
        mainFrame.setContentPane(dashboard.createPanel());
        mainFrame.revalidate();
    }
}
