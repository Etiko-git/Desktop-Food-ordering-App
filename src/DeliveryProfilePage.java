import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeliveryProfilePage extends JFrame {
    private String username;

    public DeliveryProfilePage(String username) {
        this.username = username;

        // Frame Configuration
        setTitle("Delivery Profile Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        add(contentPanel, BorderLayout.CENTER);

        JLabel profileLabel = new JLabel("Update Profile", JLabel.CENTER);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 24));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(profileLabel);

        // Update Profile Section
        JPanel profilePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(18);
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(20);

        profilePanel.add(nameLabel);
        profilePanel.add(nameField);
        profilePanel.add(phoneLabel);
        profilePanel.add(phoneField);

        // Button to update profile
        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> {
            String fullName = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (fullName.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                updateProfile(username, fullName, phone);
            }
        });

        // Change Password Section
        JLabel changePasswordLabel = new JLabel("Change Password", JLabel.CENTER);
        changePasswordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        changePasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel passwordPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel oldPasswordLabel = new JLabel("Old Password:");
        JPasswordField oldPasswordField = new JPasswordField(20);
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField(20);

        passwordPanel.add(oldPasswordLabel);
        passwordPanel.add(oldPasswordField);
        passwordPanel.add(newPasswordLabel);
        passwordPanel.add(newPasswordField);

        // Button to change password
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword()).trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                changePassword(username, oldPassword, newPassword);
            }
        });

        // Add components to content panel
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(profilePanel);
        contentPanel.add(updateProfileButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(changePasswordLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(passwordPanel);
        contentPanel.add(changePasswordButton);

        setVisible(true);
    }

    private void addNavigationButtons(JPanel navPanel) {
        String[] buttonNames = {"Home", "Request", "Update Category", "Balance", "Profile", "History", "Logout"};
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
                    new DeliveryHomePage(username).setVisible(true);
                    dispose();
                    break;
              //  case "Request":
                //    new DeliveryRequestPage(username, location).setVisible(true);
                  //  dispose();
                   // break;
                /*case "Update Category":
                    new UpdateCategoryPage(username).setVisible(true);
                    dispose();
                    break;
                case "Balance":
                   // new DeliveryBalancePage(username).setVisible(true);
                    dispose();
                    break;
              */  case "Profile":
                    new DeliveryProfilePage(username).setVisible(true);
                    dispose();
                    break;
               // case "History":
                   // new DeliveryHistoryPage(username).setVisible(true);
                   // dispose();
                 //   break;
                case "Logout":
                    int confirm = JOptionPane.showConfirmDialog(DeliveryProfilePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        new DeliveryLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    private void updateProfile(String username, String fullName, String phone) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "UPDATE delivery_person SET full_name = ?, phone = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword(String username, String oldPassword, String newPassword) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "SELECT password FROM delivery_person WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String currentPassword = rs.getString("password");
                if (currentPassword.equals(oldPassword)) {
                    String updateQuery = "UPDATE delivery_person SET password = ? WHERE username = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setString(1, newPassword);
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Old password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while changing the password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DeliveryProfilePage("John").setVisible(true);
        });
    }
}
