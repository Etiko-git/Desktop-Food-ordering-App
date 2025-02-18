import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateItemForm extends JFrame {
    private String username;
    private String category;
    private JTextField nameField;
    private JTextField imagePathField;
    private JTextField priceField;

    public UpdateItemForm(String username, String category) {
        this.username = username;
        this.category = category;

        setTitle("Update " + category);
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation buttons at the top
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] buttonNames = {"Home", "Orders", "Update Category", "Payments", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.addActionListener(new NavigationButtonListener(name));
            navPanel.add(button);
        }
        add(navPanel, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        add(formPanel, BorderLayout.CENTER);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Image Path:"));
        imagePathField = new JTextField();
        formPanel.add(imagePathField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new UpdateButtonListener());
        add(updateButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String imagePath = imagePathField.getText();
            String price = priceField.getText();

            if (updateItemInDatabase(name, imagePath, price)) {
                JOptionPane.showMessageDialog(UpdateItemForm.this, category + " updated successfully!");
                dispose();
                new UpdateCategoryPage(username).setVisible(true); // Go back to category page
            } else {
                JOptionPane.showMessageDialog(UpdateItemForm.this, "An error occurred while updating the item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean updateItemInDatabase(String name, String imagePath, String price) {
        String tableName = category.toLowerCase(); // Assuming table names are in lowercase
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "INSERT INTO " + tableName + " (name, image_path, price) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, imagePath);
            stmt.setString(3, price);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
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
                    dispose();
                    new UpdateCategoryPage(username).setVisible(true);
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
                    int response = JOptionPane.showConfirmDialog(UpdateItemForm.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new AdminLoginPage().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new UpdateItemForm("AdminUser", "Burger").setVisible(true);
    }
}
