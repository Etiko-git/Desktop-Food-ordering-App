import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateCategoryPage extends JFrame {
    private String username;

    public UpdateCategoryPage(String username) {
        this.username = username;

        setTitle("Update Category Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add navigation buttons at the top
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] buttonNames = {"Home", "Orders", "Update Category", "Payments", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.addActionListener(new NavigationButtonListener(name));
            navPanel.add(button);
        }
        add(navPanel, BorderLayout.NORTH);

        // Create buttons for each food category
        JPanel categoryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        String[] categories = {"Burger", "Starch", "Pizza", "Biryani", "Pasta", "Shawarma", "Ice", "Pav", "Soup", "Cake"};
        for (String category : categories) {
            JButton button = new JButton(category);
            button.addActionListener(new CategoryButtonListener(category));
            categoryPanel.add(button);
        }

        add(categoryPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private class CategoryButtonListener implements ActionListener {
        private String category;

        public CategoryButtonListener(String category) {
            this.category = category;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new UpdateItemForm(username, category).setVisible(true);
            dispose();
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
                    dispose();
                    new AdminHomePage(username).setVisible(true);
                    break;
                case "Orders":
                    dispose();
                    new AdminOrdersPage(username).setVisible(true);
                    break;
                case "Update Category":
                    // Do nothing, already on Update Category Page
                    break;
                case "Payments":
                    dispose();
                    new AdminPaymentsPage(username).setVisible(true);
                    break;
                case "Profile":
                    dispose();
                    new AdminProfilePage(username).setVisible(true);
                    break;
                case "History":
                    dispose();
                    new AdminHistoryPage(username).setVisible(true);
                    break;
                case "Logout":
                    int response = JOptionPane.showConfirmDialog(UpdateCategoryPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new UpdateCategoryPage("AdminUser").setVisible(true);
    }
}
