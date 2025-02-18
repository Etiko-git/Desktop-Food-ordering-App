import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProfilePage extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField;
    private int userId;

    public ProfilePage(int userId) {
        this.userId = userId;
        setTitle("Profile");
        setSize(1350, 770);
        setLayout(new BorderLayout());

        JPanel profilePanel = new JPanel(new GridLayout(0, 2));
        
        profilePanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        profilePanel.add(firstNameField);

        profilePanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        profilePanel.add(lastNameField);

        profilePanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        profilePanel.add(usernameField);

        profilePanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        profilePanel.add(emailField);

        JButton updateButton = new JButton("Update Profile");
        updateButton.addActionListener(e -> updateProfile());
        profilePanel.add(updateButton);
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        profilePanel.add(changePasswordButton);

        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);

        add(navPanel, BorderLayout.NORTH);
        add(profilePanel, BorderLayout.CENTER);

        loadUserProfile();
        setVisible(true);
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
                    int response = JOptionPane.showConfirmDialog(ProfilePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    private void loadUserProfile() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProfile() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String updateQuery = "UPDATE users SET first_name = ?, last_name = ?, username = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void changePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        
        int result = JOptionPane.showConfirmDialog(this, new Object[]{
                "Old Password:", oldPasswordField,
                "New Password:", newPasswordField
            }, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());

            // Authenticate old password and update with new hashed password
            // Assuming the password hashing and verification functions are implemented
        }
    }
}
