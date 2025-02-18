import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeliveryHomePage extends JFrame {
    private String username;

    public DeliveryHomePage(String username) {
        this.username = username;

        // Frame Configuration
        setTitle("Delivery Home Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Welcome Panel
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(welcomeLabel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 18));
        logoutButton.addActionListener(new LogoutButtonListener());
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addNavigationButtons(JPanel navPanel) {
        String[] buttonNames = {"Home", "Request", "Update Category", "Balance", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
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
                    JOptionPane.showMessageDialog(DeliveryHomePage.this, "You are already on the Home page.");
                    break;
                // case "Request":
                //   new DeliveryRequestPage(username).setVisible(true);
                //   dispose();
                //   break;
                //case "Update Category":
                 //   new UpdateCategoryPage(username).setVisible(true);
                    //dispose();
                   // break;
                /* case "Balance":
                    new DeliveryBalancePage(username).setVisible(true);
                    dispose();
                    break;
                */
                case "Profile":
                    new DeliveryProfilePage(username).setVisible(true);
                    dispose();
                    break;
                /*
                case "History":
                    new DeliveryHistoryPage(username).setVisible(true);
                    dispose();
                    break;
                */
                case "Logout":
                    int confirm = JOptionPane.showConfirmDialog(DeliveryHomePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        new DeliveryLoginPage().setVisible(true);
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(DeliveryHomePage.this, "Invalid navigation option.");
            }
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(DeliveryHomePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new DeliveryLoginPage().setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DeliveryHomePage("").setVisible(true);
        });
    }
}
