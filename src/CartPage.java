import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;

public class CartPage extends JFrame {
    private int userId;
    private JPanel itemPanel;
    private JLabel totalLabel;
    private double totalAmount = 0;

    public CartPage(int userId) {
        this.userId = userId;
        setTitle("Shopping Cart");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create navigation panel
        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);
        add(navPanel, BorderLayout.NORTH);

        // Initialize the total label
        totalLabel = new JLabel("Total: $0.00");

        // Item panel to display cart items
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        // Load items into the cart
        loadCartItems();

        // Scroll pane for the items
        JScrollPane scrollPane = new JScrollPane(itemPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Proceed button
        JButton proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProceedPage(userId, totalAmount).setVisible(true);
                dispose();
            }
        });

        // Bottom panel for total label and proceed button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(proceedButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
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
                    int response = JOptionPane.showConfirmDialog(CartPage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    private void loadCartItems() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String getCategories = "SELECT DISTINCT category FROM cart WHERE user_id = ?";
            PreparedStatement categoryStmt = conn.prepareStatement(getCategories);
            categoryStmt.setInt(1, userId);
            ResultSet categoryRs = categoryStmt.executeQuery();

            totalAmount = 0;

            // Predefined category-table mapping
            Map<String, String> categoryTableMap = new HashMap<>();
            categoryTableMap.put("starch", "starch");
            categoryTableMap.put("ice", "ice");
            categoryTableMap.put("soup", "soup");
            categoryTableMap.put("biryani", "biryani");
            categoryTableMap.put("pav", "pav");
            categoryTableMap.put("pasta", "pasta");
            categoryTableMap.put("cake", "cake");
            categoryTableMap.put("shawarma", "shawarma");
            categoryTableMap.put("burger", "burger");
            categoryTableMap.put("pizza", "pizza");

            while (categoryRs.next()) {
                String category = categoryRs.getString("category").toLowerCase();

                // Check if the category exists in the map
                if (categoryTableMap.containsKey(category)) {
                    String tableName = categoryTableMap.get(category);

                    // Build the query dynamically for the given category
                    String query = "SELECT cart.item_id, cart.quantity, " +
                            "c.name, c.image_path, c.price " +
                            "FROM cart " +
                            "JOIN " + tableName + " c ON cart.item_id = c.id " +
                            "WHERE cart.user_id = ? AND cart.category = ?";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, userId);
                    stmt.setString(2, category);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int itemId = rs.getInt("item_id");
                        String itemName = rs.getString("name");
                        String imagePath = rs.getString("image_path");
                        double price = rs.getDouble("price");
                        AtomicInteger quantity = new AtomicInteger(rs.getInt("quantity"));
                        double itemTotal = price * quantity.get();

                        totalAmount += itemTotal;

                        JPanel singleItemPanel = new JPanel(new BorderLayout());
                        JLabel imageLabel = new JLabel(new ImageIcon(
                                new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)
                        ));
                        singleItemPanel.add(imageLabel, BorderLayout.WEST);

                        JPanel detailsPanel = new JPanel(new GridLayout(3, 1));
                        detailsPanel.add(new JLabel("Name: " + itemName));
                        JLabel priceLabel = new JLabel("Price: $" + itemTotal);
                        detailsPanel.add(priceLabel);
                        JLabel quantityLabel = new JLabel("Quantity: " + quantity.get());
                        detailsPanel.add(quantityLabel);
                        singleItemPanel.add(detailsPanel, BorderLayout.CENTER);

                        JPanel controlPanel = new JPanel(new FlowLayout());
                        JButton removeButton = new JButton("-");
                        JButton addButton = new JButton("+");

                        // Add and Remove button functionality
                        removeButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (quantity.get() > 1) {
                                    updateQuantity(itemId, category, -1);
                                    quantityLabel.setText("Quantity: " + quantity.decrementAndGet());
                                    totalAmount -= price;
                                    priceLabel.setText("Price: $" + (price * quantity.get()));
                                    totalLabel.setText("Total: $" + totalAmount);
                                } else {
                                    removeFromCart(itemId, category);
                                    itemPanel.remove(singleItemPanel);
                                    itemPanel.revalidate();
                                    itemPanel.repaint();
                                    totalAmount -= itemTotal;
                                    totalLabel.setText("Total: $" + totalAmount);
                                }
                            }
                        });

                        addButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateQuantity(itemId, category, 1);
                                quantityLabel.setText("Quantity: " + quantity.incrementAndGet());
                                totalAmount += price;
                                priceLabel.setText("Price: $" + (price * quantity.get()));
                                totalLabel.setText("Total: $" + totalAmount);
                            }
                        });

                        controlPanel.add(removeButton);
                        controlPanel.add(addButton);
                        singleItemPanel.add(controlPanel, BorderLayout.EAST);

                        itemPanel.add(singleItemPanel);
                    }
                }
            }
            totalLabel.setText("Total: $" + totalAmount);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while loading cart items.");
        }
    }

    private void updateQuantity(int itemId, String category, int change) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String updateQuery = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND item_id = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, change);
            stmt.setInt(2, userId);
            stmt.setInt(3, itemId);
            stmt.setString(4, category);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the quantity.");
        }
    }

    private void removeFromCart(int itemId, String category) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "DELETE FROM cart WHERE user_id = ? AND item_id = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.setString(3, category);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while removing the item from the cart.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CartPage(1).setVisible(true); // Assuming user ID 1 for testing
        });
    }
}
