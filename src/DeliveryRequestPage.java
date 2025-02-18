import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DeliveryRequestPage extends JFrame {
    private int orderId;
    private int userId;
    private String location;

    public DeliveryRequestPage(int orderId, int userId, String location) {
        this.orderId = orderId;
        this.userId = userId;
        this.location = location;

        setTitle("Delivery Request");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Label to show order details and location
        JLabel locationLabel = new JLabel("Location: " + (location != null ? location : "Location not available"));
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(locationLabel, BorderLayout.CENTER);

        // Accept and reject buttons
        JPanel buttonPanel = new JPanel();
        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");

        acceptButton.addActionListener(e -> acceptDelivery());
        rejectButton.addActionListener(e -> rejectDelivery());

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void acceptDelivery() {
        // Logic to handle acceptance
        System.out.println("Delivery accepted for order ID " + orderId);
        dispose(); // Close the page
    }

    private void rejectDelivery() {
        // Logic to handle rejection
        System.out.println("Delivery rejected for order ID " + orderId);
        dispose(); // Close the page
    }

    public static void main(String[] args) {
        new DeliveryRequestPage(1, 1, "123 Main Street");
    }
}