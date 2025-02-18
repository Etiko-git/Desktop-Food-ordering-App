import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Automatically maximize the window
        setLayout(null);

        // Background gradient panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(63, 94, 251), getWidth(), getHeight(), new Color(252, 70, 107));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Center panel for login card
        JPanel cardPanel = new JPanel();
        cardPanel.setBounds(450, 150, 450, 450);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(null);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardPanel.setOpaque(true);
        backgroundPanel.add(cardPanel);

        // Title label
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 20, 450, 40);
        cardPanel.add(titleLabel);

        // Username/Email label and field
        JLabel usernameLabel = new JLabel("Email");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setBounds(50, 100, 350, 25);
        cardPanel.add(usernameLabel);

        usernameOrEmailField = new JTextField();
        usernameOrEmailField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameOrEmailField.setBounds(50, 130, 350, 35);
        cardPanel.add(usernameOrEmailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setBounds(50, 190, 350, 25);
        cardPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBounds(50, 220, 350, 35);
        cardPanel.add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(50, 290, 350, 40);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(null);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Gradient effect for button
        loginButton.setBackground(new Color(63, 94, 251));
        loginButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(63, 94, 251), c.getWidth(), c.getHeight(), new Color(252, 70, 107));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                super.paint(g, c);
            }
        });
        loginButton.addActionListener(new LoginAction());
        cardPanel.add(loginButton);

        // Footer text
        JLabel footerLabel = new JLabel("Don't have an account? Sign up");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setBounds(0, 360, 450, 30);
        footerLabel.setForeground(new Color(63, 94, 251));
        footerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openRegistrationForm(); // Open RegistrationForm on click
            }
        });
        cardPanel.add(footerLabel);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String usernameOrEmail = usernameOrEmailField.getText();
            String password = new String(passwordField.getPassword());

            // Database connection and user validation
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
                String query = "SELECT id, password_hash FROM users WHERE (username = ? OR email = ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, usernameOrEmail);
                stmt.setString(2, usernameOrEmail);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String storedHash = rs.getString("password_hash");

                    if (hashPassword(password).equals(storedHash)) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Login successful!");
                        openHomePage(userId); // Redirect to HomePage with userId
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this, "Invalid password.");
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "User not found.");
                }
            } catch (SQLException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginForm.this, "An error occurred during login.");
            }
        }
    }

    // Method to hash password using SHA-256
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Redirect to HomePage upon successful login with userId
    private void openHomePage(int userId) {
        new HomePage(userId).setVisible(true);
        dispose(); // Close the LoginForm
    }
    
 // Redirect to RegistrationForm
    private void openRegistrationForm() {
        new RegistrationForm().setVisible(true);
        dispose(); // Close the LoginForm
    }

    // Main method to run LoginForm
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
