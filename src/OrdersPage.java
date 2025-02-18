import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrdersPage extends JFrame {
    private int userId;

    public OrdersPage(int userId) {
        this.userId = userId;
        setTitle("Orders - Contact Us");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container panel with BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(171, 139, 122));

        // Navigation panel at the top
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(171, 139, 122));

        JPanel leftNavBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 30));
        leftNavBar.setBackground(new Color(171, 139, 122));
        addNavigationText(leftNavBar, false);

        JPanel rightNavBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 30));
        rightNavBar.setBackground(new Color(171, 139, 122));
        addNavigationText(rightNavBar, true);

        JLabel titleLabel = new JLabel("<html><center>SAJEF<br>RESTAURANT</center></html>");
        titleLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        navBar.add(leftNavBar, BorderLayout.WEST);
        navBar.add(titleLabel, BorderLayout.CENTER);
        navBar.add(rightNavBar, BorderLayout.EAST);

        mainPanel.add(navBar, BorderLayout.NORTH);

        // Header label
        JLabel headerLabel = new JLabel("Contact Us for Order Issues", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        mainPanel.add(headerLabel, BorderLayout.CENTER); // Add header at the center (but top)

        // Form panel for complaint input
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(171, 139, 122)); // Match background color

        JLabel complaintLabel = new JLabel("Your Complaint:");
        complaintLabel.setForeground(Color.WHITE);
        JTextArea complaintField = new JTextArea();
        complaintField.setLineWrap(true);
        complaintField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(complaintField);

        formPanel.add(complaintLabel, BorderLayout.NORTH);
        formPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER); // Add form panel to the center

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(255, 165, 0)); // Orange background for visibility
        submitButton.setForeground(Color.WHITE); // White text
        submitButton.setFocusPainted(false); // Remove focus border
        submitButton.addActionListener(e -> {
            String complaint = complaintField.getText().trim();
            if (complaint.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your complaint.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (submitComplaint(complaint)) {
                    complaintField.setText(""); // Clear the text area if submission is successful
                }
            }
        });

        // Button panel for submit button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(171, 139, 122)); // Match background color
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Place submit button at the bottom

        setContentPane(mainPanel);
        setVisible(true); // Ensure visibility
    }

    private void addNavigationText(JPanel navPanel, boolean isLogout) {
        String[] buttonNames = isLogout ? new String[]{"Logout"} : new String[]{"Home", "Orders", "Favourites", "Cart", "Profile", "History"};
        for (String name : buttonNames) {
            JLabel label = new JLabel(name);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));

            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    label.setForeground(new Color(255, 255, 204));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    label.setForeground(Color.WHITE);
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    handleNavigation(name);
                }
            });

            navPanel.add(label);
        }
    }

    private void handleNavigation(String name) {
        switch (name) {
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
                int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginForm().setVisible(true);
                }
                break;
        }
    }

    private boolean submitComplaint(String complaint) {
        String dbUrl = "jdbc:mysql://localhost:3306/food";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO complain (user_id, username, complained) VALUES (?, ?, ?)")) {

            // Fetching username using userId (if needed)
            String username = getUsernameById(conn, userId);

            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, complaint);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "We are sorry for the inconvenience, Our team will get back to you shortly!");
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit your complaint. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String getUsernameById(Connection conn, int userId) throws SQLException {
        String query = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        }
        return "Unknown";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrdersPage(1).setVisible(true)); // Replace 1 with an actual userId
    }
}