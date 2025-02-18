import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

public class AdminPaymentsPage extends JFrame {
    private DefaultTableModel tableModel;

    public AdminPaymentsPage(String username) {
        setTitle("Admin Payments Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel, username);
        add(navPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"user_id", "order_id", "amount", "payment_date"});
        JTextField filterTextField = new JTextField(20);
        JButton filterButton = new JButton("Filter");
        filterPanel.add(new JLabel("Filter By:"));
        filterPanel.add(filterComboBox);
        filterPanel.add(filterTextField);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.SOUTH);

        // Payments table
        tableModel = new DefaultTableModel(new String[]{"ID", "User ID", "Order ID", "Amount", "Date"}, 0);
        JTable paymentsTable = new JTable(tableModel);
        add(new JScrollPane(paymentsTable), BorderLayout.CENTER);

        // Load payments initially
        loadPayments();

        // Filter button functionality
        filterButton.addActionListener(e -> {
            String filterColumn = filterComboBox.getSelectedItem().toString();
            String filterValue = filterTextField.getText().trim();
            if (!filterValue.isEmpty()) {
                filterPayments(filterColumn, filterValue);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a value to filter by.");
            }
        });

        setVisible(true);
    }

    private void loadPayments() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM payments")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int orderId = rs.getInt("order_id");
                double amount = rs.getDouble("amount");
                Timestamp paymentDate = rs.getTimestamp("payment_date");

                tableModel.addRow(new Object[]{id, userId, orderId, amount, paymentDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }
    }

    private void filterPayments(String filterColumn, String filterValue) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {

            // Validate filter column
            String[] validColumns = {"user_id", "order_id", "amount", "payment_date"};
            if (!Arrays.asList(validColumns).contains(filterColumn)) {
                throw new IllegalArgumentException("Invalid column for filtering");
            }

            String query = "SELECT * FROM payments WHERE " + filterColumn + " LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + filterValue + "%");

                try (ResultSet rs = stmt.executeQuery()) {
                    tableModel.setRowCount(0); // Clear existing rows
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int userId = rs.getInt("user_id");
                        int orderId = rs.getInt("order_id");
                        double amount = rs.getDouble("amount");
                        Timestamp paymentDate = rs.getTimestamp("payment_date");

                        tableModel.addRow(new Object[]{id, userId, orderId, amount, paymentDate});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payments data: " + e.getMessage());
        }
    }

    private void addNavigationButtons(JPanel navPanel, String username) {
        String[] buttonNames = {"Home", "Orders", "Update Category", "Payments", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.addActionListener(new NavigationButtonListener(name, username));
            navPanel.add(button);
        }
    }

    private class NavigationButtonListener implements ActionListener {
        private String buttonName;
        private String username;

        public NavigationButtonListener(String buttonName, String username) {
            this.buttonName = buttonName;
            this.username = username;
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
                    int response = JOptionPane.showConfirmDialog(AdminPaymentsPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPaymentsPage("admin").setVisible(true));
    }
}
