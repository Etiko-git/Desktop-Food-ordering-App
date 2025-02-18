import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DeliveryLoginPage extends JFrame {
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;

    public DeliveryLoginPage() {
        setTitle("Delivery Login");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Adding username or email label and text field
        add(new JLabel("Username or Email:"));
        usernameOrEmailField = new JTextField();
        add(usernameOrEmailField);

        // Adding password label and password field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Login button with action listener
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());
        add(loginButton);

        // Register button with action listener to navigate to the registration page
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            dispose(); // Close the login window
            new DeliveryRegistrationPage().setVisible(true); // Open the registration page
        });
        add(registerButton);

        setVisible(true); // Make the login page visible
    }

    // Method to hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); // Return the hashed password as a string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e); // Handle exception
        }
    }

    // Method to handle user login process
    private void loginUser() {
        String usernameOrEmail = usernameOrEmailField.getText().trim(); // Get the username or email input
        String password = new String(passwordField.getPassword()); // Get the password input
        String hashedPassword = hashPassword(password); // Hash the password

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM delivery_persons WHERE (username = ? OR email = ?) AND password = ?")) {

            stmt.setString(1, usernameOrEmail); // Set username or email parameter
            stmt.setString(2, usernameOrEmail); // Set username or email again for the query
            stmt.setString(3, hashedPassword); // Set hashed password parameter

            ResultSet rs = stmt.executeQuery(); // Execute the query

            // If the result set has data, login is successful
            if (rs.next()) {
                String firstName = rs.getString("first_name"); // Get first name from the result set
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + firstName + "."); // Show success message
                dispose(); // Close the login window
                new DeliveryHomePage(firstName).setVisible(true); // Open the home page of the delivery person
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username/email or password."); // Invalid login message
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login: " + e.getMessage()); // Show error message if SQL fails
        }
    }

    // Action listener for the login button
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loginUser(); // Call loginUser when the login button is pressed
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeliveryLoginPage::new); // Launch the DeliveryLoginPage
    }
}
