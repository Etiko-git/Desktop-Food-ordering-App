import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminProfilePage extends JFrame {
    private String username;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public AdminProfilePage(String username) {
        this.username = username;

        setTitle("Admin Profile Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Profile form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(username);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("New Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new SaveButtonListener());
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
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
                    dispose();
                    new AdminHomePage(username).setVisible(true);
                    break;
                case "Orders":
                    dispose();
                    new AdminOrdersPage(username).setVisible(true);
                    break;
                case "Update Category":
                    dispose();
                    new UpdateCategoryPage(username).setVisible(true);
                    break;
                case "Payments":
                    dispose();
                    new AdminPaymentsPage(username).setVisible(true);
                    break;
                case "Profile":
                    // Do nothing, already on Profile Page
                    break;
                case "History":
                    dispose();
                    new AdminHistoryPage(username).setVisible(true);
                    break;
                case "Logout":
                    int response = JOptionPane.showConfirmDialog(AdminProfilePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(AdminProfilePage.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (updateAdminProfile(newUsername, newPassword)) {
                JOptionPane.showMessageDialog(AdminProfilePage.this, "Profile updated successfully!");
                dispose();
                new AdminHomePage(newUsername).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(AdminProfilePage.this, "An error occurred while updating the profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean updateAdminProfile(String newUsername, String newPassword) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "UPDATE admins SET username = ?, password = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, newUsername);
            stmt.setString(2, hashPassword(newPassword));  // Hash the new password
            stmt.setString(3, username);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                username = newUsername;  // Update local username variable
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String hashPassword(String password) {
        // Simple hashing function - for demonstration only
        return Integer.toHexString(password.hashCode());
    }

    // Main method for testing
    public static void main(String[] args) {
        new AdminProfilePage("AdminUser").setVisible(true);
    }
}
