import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PlaceOrderPage extends JFrame {
    private int userId;
    private double totalAmount;
    private JLabel orderImageLabel;
    private JTextField locationField; // Field for location input

    public PlaceOrderPage(int userId, double totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;

        setTitle("Place Order");
        setSize(1350, 770);
        setLayout(new BorderLayout());

        // Create navigation panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Center panel to hold the image and location input
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));  // Arrange vertically
        orderImageLabel = new JLabel();
        fetchOrderImage();
        centerPanel.add(orderImageLabel);  // Add image
        centerPanel.add(Box.createVerticalStrut(10));  // Add space between image and location input
        locationField = new JTextField(20);  // JTextField for location input
        centerPanel.add(new JLabel("Enter your location:"));  // Label for location input
        centerPanel.add(locationField);  // Add location field
        add(centerPanel, BorderLayout.CENTER);

        // Button panel for confirm and cancel actions
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Confirm Payment");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Confirm action
        confirmButton.addActionListener(e -> {
            String location = locationField.getText();  // Get location from the field
            if (location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your location.");
                return;
            }

            int orderId = createOrder(location);  // Pass the location to create the order
            if (orderId > 0) {
                sendPaymentRequest(orderId);
                JOptionPane.showMessageDialog(this, "Payment request sent to admin. Please wait for confirmation.");
                waitForConfirmation(orderId);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create order.");
            }
        });

        // Cancel action
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void fetchOrderImage() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT image_path FROM orderimage WHERE id = 1")) {
            if (rs.next()) {
                String imagePath = rs.getString("image_path");
                // Create an ImageIcon from the path and set the label
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                orderImageLabel.setIcon(new ImageIcon(image));
            } else {
                orderImageLabel.setText("No image available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            orderImageLabel.setText("Failed to load image.");
        }
    }

    private void sendPaymentRequest(int orderId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO payment_requests (user_id, order_id, amount, status) VALUES (?, ?, ?, 'Pending')")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, orderId);
            stmt.setDouble(3, totalAmount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int createOrder(String location) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO orders (user_id, order_date, item_name, total_amount, location) VALUES (?, NOW(), ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, "Default Item Name");
            stmt.setDouble(3, totalAmount);
            stmt.setString(4, location);  // Save the location entered by the user

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void waitForConfirmation(int orderId) {
        new Thread(() -> {
            while (true) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT status FROM payment_requests WHERE order_id = ?")) {
                    stmt.setInt(1, orderId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && "Received".equals(rs.getString("status"))) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Payment confirmed! Redirecting to history.");
                                new HistoryPage(userId).setVisible(true);
                                dispose();
                                // Send location to DeliveryRequestPage
                                new DeliveryRequestPage(orderId, userId, getOrderLocation(orderId)).setVisible(true);
                            });
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(5000); // Wait for 5 seconds before polling again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getOrderLocation(int orderId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT location FROM orders WHERE id = ?")) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("location");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void addNavigationButtons(JPanel navPanel) {
        String[] buttonNames = {"Home", "Orders", "Favourites", "Cart", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.addActionListener(new NavigationButtonListener(name));
            navPanel.add(button);
        }
    }

    private class NavigationButtonListener implements ActionListener {
        private String buttonName;

        public NavigationButtonListener(String buttonName) {
            this.buttonName = buttonName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (buttonName) {
                case "Home":
                    new HomePage(userId).setVisible(true);
                    dispose();
                    break;
                case "Orders":
                    new OrdersPage(userId).setVisible(true);
                    dispose();
                    break;
                case "Favourites":
                    new FavoritePage(userId).setVisible(true);
                    dispose();
                    break;
                case "Cart":
                    new CartPage(userId).setVisible(true);
                    dispose();
                    break;
                case "Profile":
                    new ProfilePage(userId).setVisible(true);
                    dispose();
                    break;
                case "History":
                    new HistoryPage(userId).setVisible(true);
                    dispose();
                    break;
                case "Logout":
                    int response = JOptionPane.showConfirmDialog(PlaceOrderPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlaceOrderPage(1, 100.0)); // Test userId = 1, totalAmount = 100.0
    }
}
