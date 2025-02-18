import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HistoryPage extends JFrame {
    private int userId;

    public HistoryPage(int userId) {
        this.userId = userId;
        setTitle("Order History");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation panel at the top
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Main area to display order history
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // Load history when the page is loaded
        loadOrderHistory(historyArea);

        setVisible(true);
    }

    private void loadOrderHistory(JTextArea historyArea) {
        historyArea.setText(""); // Clear previous content
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT item_name, order_date, total_amount FROM orders WHERE user_id = " + userId + " ORDER BY order_date DESC")) {

            boolean hasOrders = false; // Flag to check if any records exist

            while (rs.next()) {
                hasOrders = true;
                String itemName = rs.getString("item_name");
                String orderDate = rs.getString("order_date");
                double totalAmount = rs.getDouble("total_amount");

                historyArea.append("Item: " + itemName + ", Date: " + orderDate + ", Amount: $" + totalAmount + "\n");
            }

            if (!hasOrders) {
                historyArea.append("No orders found for the user.\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            historyArea.append("Error loading order history: " + ex.getMessage() + "\n");
        }
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
                    int response = JOptionPane.showConfirmDialog(HistoryPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HistoryPage(1).setVisible(true)); // Replace 1 with an actual userId
    }
}
