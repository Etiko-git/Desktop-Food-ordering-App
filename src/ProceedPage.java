import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProceedPage extends JFrame {
    private int userId;
    private double totalAmount;

    public ProceedPage(int userId, double totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;

        setTitle("Order Summary");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Main panel with total amount
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel totalLabel = new JLabel("Total Amount Payable: $" + totalAmount, SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(totalLabel, BorderLayout.CENTER);

        // Order button
        JButton orderButton = new JButton("Place Order");
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ProceedPage.this, "Order placed successfully!");
                new PlaceOrderPage(userId, totalAmount).setVisible(true);
                dispose();
            }
        });
        mainPanel.add(orderButton, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
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
                    int response = JOptionPane.showConfirmDialog(ProceedPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new ProceedPage(1, 20.50).setVisible(true);
    }
}