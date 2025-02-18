import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminRegistrationPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;

    public AdminRegistrationPage() {
        setTitle("Admin Registration");
        setSize(1350, 770);
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterAction());
        add(registerButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(AdminRegistrationPage.this, "Passwords do not match!");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword != null) {
                registerAdmin(username, hashedPassword);
            } else {
                JOptionPane.showMessageDialog(AdminRegistrationPage.this, "Error hashing password.");
            }
        }

        private String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = md.digest(password.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hashedBytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        private void registerAdmin(String username, String hashedPassword) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
                String query = "INSERT INTO admins (username, password) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(AdminRegistrationPage.this, "Admin registered successfully!");
                dispose();
                new AdminLoginPage().setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminRegistrationPage.this, "Error registering admin.");
            }
        }
    }

    public static void main(String[] args) {
        new AdminRegistrationPage();
    }
}
