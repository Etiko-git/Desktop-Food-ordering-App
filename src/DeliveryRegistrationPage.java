import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DeliveryRegistrationPage extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField, phoneField;
    private JComboBox<String> countryCodeCombo;
    private JPasswordField passwordField, confirmPasswordField;

    public DeliveryRegistrationPage() {
        setTitle("Delivery Registration");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2, 10, 10));

        // Adding form fields
        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Country Code:"));
        String[] countryCodes = {"+1", "+44", "+91", "+234", "+61"};
        countryCodeCombo = new JComboBox<>(countryCodes);
        add(countryCodeCombo);

        add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterActionListener());
        add(registerButton);

        setVisible(true); // Make the registration page visible
    }

    // Validate email format using regex
    private boolean isEmailValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // Validate password strength
    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@*/]).{8,}$");
    }

    // Validate phone number to ensure it's numeric and contains only digits
    private boolean isPhoneNumberValid(String phone) {
        return phone.matches("\\d{10}"); // Validates that the phone number is exactly 10 digits long
    }

    // Hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Action listener for the Register button
    private class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Check if any field is empty
            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "All fields are required.");
                return;
            }

            // Validate email format
            if (!isEmailValid(email)) {
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Invalid email format.");
                return;
            }

            // Validate password format
            if (!isPasswordValid(password)) {
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Password must start with an uppercase letter, contain at least 8 characters, include a number, and one special character (@, *, or /).");
                return;
            }

            // Ensure passwords match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Passwords do not match.");
                return;
            }

            // Validate phone number format (10 digits)
            if (!isPhoneNumberValid(phone)) {
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Phone number must be 10 digits.");
                return;
            }

            // Hash the password
            String hashedPassword = hashPassword(password);

            // Store the delivery person in the database
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO delivery_persons (first_name, last_name, username, email, phone, password) VALUES (?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, username);
                stmt.setString(4, email);
                stmt.setString(5, phone);
                stmt.setString(6, hashedPassword);

                // Execute the insert statement
                stmt.executeUpdate();

                // Registration success message
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Registration successful!");
                dispose(); // Close the registration page
                new DeliveryLoginPage().setVisible(true); // Open the login page
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(DeliveryRegistrationPage.this, "Error registering delivery person: " + ex.getMessage());
            }
        }
    }

    // Main method to start the registration page
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeliveryRegistrationPage::new); // Launch the registration page
    }
}
