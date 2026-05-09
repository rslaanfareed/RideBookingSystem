package com.ridebooking.controller;

import com.ridebooking.model.Driver;
import com.ridebooking.model.Passenger;
import com.ridebooking.model.Ride;
import com.ridebooking.service.DriverManagementService;
import com.ridebooking.service.FareCalculationService;
import com.ridebooking.service.PassengerManagementService;
import com.ridebooking.service.RideBookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatisticsController {
    private Passenger currentPassenger;
    private PassengerManagementService passengerService;
    private DriverManagementService driverService;
    private RideBookingService rideService;
    private FareCalculationService fareService;
    private JFrame mainFrame;

    public StatisticsController(Passenger currentPassenger,
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
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("System Statistics", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setBackground(new Color(149, 165, 166));
        backBtn.setForeground(Color.WHITE);
        header.add(backBtn, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(236, 240, 241));
        center.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel summaryTitle = new JLabel("Summary");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 16));
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passengerCount = new JLabel("Total Passengers: " + passengerService.getTotalPassengers());
        JLabel driverCount = new JLabel("Total Drivers: " + driverService.getTotalDrivers());
        JLabel rideCount = new JLabel("Total Rides: " + rideService.getTotalRides());

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.Y_AXIS));
        summaryBox.setBackground(Color.WHITE);
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(44, 62, 80), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        summaryBox.add(summaryTitle);
        summaryBox.add(Box.createVerticalStrut(10));
        summaryBox.add(passengerCount);
        summaryBox.add(driverCount);
        summaryBox.add(rideCount);
        summaryBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel driversPanel = new JPanel(new BorderLayout());
        driversPanel.setBackground(Color.WHITE);
        driversPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel driversLabel = new JLabel("Available Drivers");
        driversLabel.setFont(new Font("Arial", Font.BOLD, 14));
        driversPanel.add(driversLabel, BorderLayout.NORTH);

        String[] driverColumns = {"Name", "Ride Type", "Rating", "Status"};
        DefaultTableModel driversModel = new DefaultTableModel(driverColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        List<Driver> drivers = driverService.getAllDrivers();
        for (Driver driver : drivers) {
            driversModel.addRow(new Object[]{
                    driver.getName(),
                    driver.getRideType().getDescription(),
                    driver.getRating(),
                    driver.isAvailable() ? "Available" : "Busy"
            });
        }
        JTable driversTable = new JTable(driversModel);
        driversTable.setFillsViewportHeight(true);
        driversPanel.add(new JScrollPane(driversTable), BorderLayout.CENTER);

        JPanel ridesPanel = new JPanel(new BorderLayout());
        ridesPanel.setBackground(Color.WHITE);
        ridesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel ridesLabel = new JLabel("Recent Rides");
        ridesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ridesPanel.add(ridesLabel, BorderLayout.NORTH);

        String[] rideColumns = {"Ride ID", "Passenger", "Status", "Fare"};
        DefaultTableModel ridesModel = new DefaultTableModel(rideColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        List<Ride> rides = rideService.getAllRides();
        for (Ride ride : rides) {
            ridesModel.addRow(new Object[]{
                    ride.getRideId(),
                    ride.getPassenger().getName(),
                    ride.getStatus().getDescription(),
                    String.format("$%.2f", ride.getFare())
            });
        }
        JTable ridesTable = new JTable(ridesModel);
        ridesTable.setFillsViewportHeight(true);
        ridesPanel.add(new JScrollPane(ridesTable), BorderLayout.CENTER);

        center.add(summaryBox);
        center.add(Box.createVerticalStrut(20));
        center.add(driversPanel);
        center.add(Box.createVerticalStrut(20));
        center.add(ridesPanel);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(center), BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            DashboardController dashboard = new DashboardController(
                    currentPassenger, passengerService, driverService, rideService, fareService, mainFrame
            );
            mainFrame.setContentPane(dashboard.createPanel());
            mainFrame.revalidate();
        });

        return mainPanel;
    }
}
