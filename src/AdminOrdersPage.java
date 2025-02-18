import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminOrdersPage extends JFrame {
    private String username;

    public AdminOrdersPage(String username) {
        this.username = username;

        setTitle("Admin Orders Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display pending orders
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        loadPendingOrders(orderPanel);
        add(new JScrollPane(orderPanel), BorderLayout.CENTER);

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    private void loadPendingOrders(JPanel orderPanel) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT request_id, user_id, order_id, amount FROM payment_requests WHERE status = 'Pending'")) {

            while (rs.next()) {
                int requestId = rs.getInt("request_id");
                int userId = rs.getInt("user_id");
                int orderId = rs.getInt("order_id");
                double amount = rs.getDouble("amount");

                JPanel orderRow = new JPanel(new FlowLayout());
                orderRow.add(new JLabel("User ID: " + userId + ", Order ID: " + orderId + ", Amount: $" + amount));

                JButton receivedButton = new JButton("Received");
                receivedButton.addActionListener(e -> {
                    updateRequestStatus(requestId, "Received", orderId, userId, amount);
                    orderPanel.remove(orderRow);
                    orderPanel.revalidate();
                    orderPanel.repaint();
                });

                JButton pendingButton = new JButton("Pending");
                pendingButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Payment status remains pending.");
                });

                orderRow.add(receivedButton);
                orderRow.add(pendingButton);
                orderPanel.add(orderRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRequestStatus(int requestId, String status, int orderId, int userId, double amount) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE payment_requests SET status = ? WHERE request_id = ?")) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

            if ("Received".equals(status)) {
                JOptionPane.showMessageDialog(this, "Order marked as received.");
                markOrderAsSuccessful(orderId, userId);
                logPayment(userId, orderId, amount); // Log the payment
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markOrderAsSuccessful(int orderId, int userId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE orders SET status = 'Successful' WHERE id = ?")) {

            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logPayment(int userId, int orderId, double amount) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO payments (user_id, order_id, amount) VALUES (?, ?, ?)")) {

            stmt.setInt(1, userId);
            stmt.setInt(2, orderId);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNavigationButtons(JPanel navPanel) {
        String[] buttonNames = {"Home", "Orders", "Update Category", "Payments", "Profile", "History", "Logout"};
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
                    new AdminHomePage(username).setVisible(true);
                    dispose();
                    break;
                case "Orders":
                    new AdminOrdersPage(username).setVisible(true);
                    dispose();
                    break;
                case "Update Category":
                    new UpdateCategoryPage(username).setVisible(true);
                    dispose();
                    break;
                case "Payments":
                    new AdminPaymentsPage(username).setVisible(true);
                    dispose();
                    break;
                case "Profile":
                    new AdminProfilePage(username).setVisible(true);
                    dispose();
                    break;
                case "History":
                    new AdminHistoryPage(username).setVisible(true);
                    dispose();
                    break;
                case "Logout":
                    int response = JOptionPane.showConfirmDialog(AdminOrdersPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminOrdersPage("admin").setVisible(true));
    }
}
