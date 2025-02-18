import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrationForm extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderComboBox;
    private GradientButton registerButton;

    public RegistrationForm() {
        // Set the title of the window
        setTitle("Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Automatically maximize the window
        setLocationRelativeTo(null); // Center the window

        // Create a gradient background panel
        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints bgGbc = new GridBagConstraints();
        bgGbc.fill = GridBagConstraints.BOTH;
        bgGbc.insets = new Insets(20, 20, 20, 20);

        // Create a white frame panel for form contents
        JPanel whiteFramePanel = new JPanel();
        whiteFramePanel.setLayout(new GridBagLayout());
        whiteFramePanel.setBackground(Color.WHITE);
        whiteFramePanel.setPreferredSize(new Dimension(600, 700)); // Frame size
        whiteFramePanel.setBorder(new LineBorder(new Color(200, 200, 200), 2, true)); // Rounded border

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to the white frame
        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(91, 134, 229));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        whiteFramePanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel firstNameLabel = createStyledLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        whiteFramePanel.add(firstNameLabel, gbc);

        firstNameField = createStyledTextField();
        gbc.gridx = 1;
        whiteFramePanel.add(firstNameField, gbc);

        JLabel lastNameLabel = createStyledLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        whiteFramePanel.add(lastNameLabel, gbc);

        lastNameField = createStyledTextField();
        gbc.gridx = 1;
        whiteFramePanel.add(lastNameField, gbc);

        JLabel usernameLabel = createStyledLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        whiteFramePanel.add(usernameLabel, gbc);

        usernameField = createStyledTextField();
        gbc.gridx = 1;
        whiteFramePanel.add(usernameField, gbc);

        JLabel emailLabel = createStyledLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        whiteFramePanel.add(emailLabel, gbc);

        emailField = createStyledTextField();
        gbc.gridx = 1;
        whiteFramePanel.add(emailField, gbc);

        JLabel genderLabel = createStyledLabel("Gender:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        whiteFramePanel.add(genderLabel, gbc);

        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        styleComboBox(genderComboBox);
        gbc.gridx = 1;
        whiteFramePanel.add(genderComboBox, gbc);

        JLabel passwordLabel = createStyledLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        whiteFramePanel.add(passwordLabel, gbc);

        passwordField = createStyledPasswordField();
        gbc.gridx = 1;
        whiteFramePanel.add(passwordField, gbc);

        JLabel confirmPasswordLabel = createStyledLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        whiteFramePanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = createStyledPasswordField();
        gbc.gridx = 1;
        whiteFramePanel.add(confirmPasswordField, gbc);

        registerButton = new GradientButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        whiteFramePanel.add(registerButton, gbc);

        JLabel footerLabel = new JLabel("Already registered? Login", SwingConstants.CENTER);
        footerLabel.setForeground(Color.BLUE);
        footerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate to LoginForm
            	new LoginForm().setVisible(true);// Replace with actual LoginForm instantiation
                dispose(); // Close the registration form
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        whiteFramePanel.add(footerLabel, gbc);

        // Add the white frame panel to the background panel
        bgGbc.gridx = 0;
        bgGbc.gridy = 0;
        backgroundPanel.add(whiteFramePanel, bgGbc);

        // Add the background panel to the frame
        add(backgroundPanel);

        setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18)); // Increased font size to 18
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16)); // Increased font size to 16
        textField.setPreferredSize(new Dimension(250, 30)); // Increased width and height
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16)); // Increased font size to 16
        passwordField.setPreferredSize(new Dimension(250, 30)); // Increased width and height
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passwordField.setBackground(Color.WHITE);
        return passwordField;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        comboBox.setBackground(Color.WHITE);
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }

    // Gradient background for the frame
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(91, 134, 229);
            Color color2 = new Color(255, 0, 85);
            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Custom gradient button
    class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            GradientPaint gradient = new GradientPaint(0, 0, new Color(91, 134, 229), getWidth(), getHeight(), new Color(255, 0, 85));
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.dispose();
            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(91, 134, 229));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2d.dispose();
        }
    }
}
