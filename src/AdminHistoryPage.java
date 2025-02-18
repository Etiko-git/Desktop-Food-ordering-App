import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminHistoryPage extends JFrame {
    private String username;

    public AdminHistoryPage(String username) {
        this.username = username;
        setTitle("Admin Home Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome back, " + username + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.CENTER);

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
                    // Do nothing, already on Home Page
                    new AdminHomePage(username).setVisible(true);
                    dispose();
                    break;
                case "Orders":
                    // Navigate to OrdersPage
                    dispose();
                    new AdminOrdersPage(username).setVisible(true);
                    break;
                case "Update Category":
                    // Navigate to UpdateCategoryPage
                    dispose();
                    new UpdateCategoryPage(username).setVisible(true);
                    break;
                case "Payments":
                    // Navigate to PaymentsPage
                    dispose();
                    new AdminPaymentsPage(username).setVisible(true);
                    break;
                case "Profile":
                    // Navigate to AdminProfilePage
                    dispose();
                    new AdminProfilePage(username).setVisible(true);
                    break;
                case "History":
                    // Navigate to HistoryPage
                    dispose();
                    new AdminHistoryPage(username).setVisible(true);
                    break;
                case "Logout":
                    int response = JOptionPane.showConfirmDialog(AdminHistoryPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        new AdminHomePage("Admin"); // Replace "Admin" with the username for testing
    }
}
