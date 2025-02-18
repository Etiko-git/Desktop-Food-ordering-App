import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FavoritePage extends JFrame {
    private int userId;
    private JPanel itemPanel;
    private Connection conn;

    public FavoritePage(int userId) {
        this.userId = userId;
        setupUI();
        setupDatabaseConnection();
        loadFavoriteItems();
    }

    private void setupUI() {
        setTitle("Favorite Page");
        setSize(1350, 770);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(itemPanel);

        JPanel navPanel = new JPanel(new FlowLayout());
        addNavigationButtons(navPanel);

        add(scrollPane, BorderLayout.CENTER);
        add(navPanel, BorderLayout.NORTH);
    }

    private void addNavigationButtons(JPanel navPanel) {
        String[] buttonNames = {"Home", "Orders", "Favourites", "Cart", "Profile", "History", "Logout"};
        for (String name : buttonNames) {
            JButton button = new JButton(name);
            button.addActionListener(new NavigationButtonListener(name));
            navPanel.add(button);
        }
    }

    private void setupDatabaseConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFavoriteItems() {
        itemPanel.removeAll();

        // Adjust query to fetch item details from all item tables based on the category
        String query = "SELECT f.id, f.item_id, f.category, m.name, m.price FROM favourites f " +
                       "JOIN (SELECT id, name, price, 'burger' as category FROM burger " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'pizza' as category FROM pizza " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'starch' as category FROM starch " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'ice' as category FROM ice " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'soup' as category FROM soup " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'biryani' as category FROM biryani " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'pav' as category FROM pav " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'pasta' as category FROM pasta " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'cake' as category FROM cake " +
                       "UNION ALL " +
                       "SELECT id, name, price, 'shawarma' as category FROM shawarma) m " +
                       "ON f.item_id = m.id AND f.category = m.category " +
                       "WHERE f.user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int favoriteId = rs.getInt("id");
                String itemName = rs.getString("name");
                double itemPrice = rs.getDouble("price");

                JPanel singleItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                singleItemPanel.add(new JLabel(itemName + " - $" + itemPrice));

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> removeFavorite(favoriteId, singleItemPanel));

                singleItemPanel.add(removeButton);
                itemPanel.add(singleItemPanel);
            }
            itemPanel.revalidate();
            itemPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeFavorite(int favoriteId, JPanel singleItemPanel) {
        String deleteQuery = "DELETE FROM favourites WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, favoriteId);
            stmt.executeUpdate();
            itemPanel.remove(singleItemPanel);
            itemPanel.revalidate();
            itemPanel.repaint();
            JOptionPane.showMessageDialog(this, "Item removed from favorites.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToFavorite(int itemId, String category) {
        String checkQuery = "SELECT * FROM favourites f " +
                            "JOIN (SELECT id, name FROM burger UNION ALL SELECT id, name FROM pizza) m " +
                            "ON f.item_id = m.id " +
                            "WHERE f.user_id = ? AND m.name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, category); // Assume category is used as the name; change if necessary
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Item already exists in favorites.");
            } else {
                String insertQuery = "INSERT INTO favourites (user_id, item_id, category) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, itemId);
                    insertStmt.setString(3, category);
                    insertStmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Item added to favorites.");
                    loadFavoriteItems(); // Refresh the displayed items
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                    int response = JOptionPane.showConfirmDialog(FavoritePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginForm().setVisible(true);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        int userId = 1; // Change this as needed for testing
        SwingUtilities.invokeLater(() -> new FavoritePage(userId).setVisible(true));
    }
}
