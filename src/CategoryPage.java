import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.sql.*;

public class CategoryPage extends JFrame {
    private int userId;
    private String category;

    public CategoryPage(int userId, String category) {
        this.userId = userId;
        this.category = category;

        setTitle("Category - " + category);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main container panel with BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(171, 139, 122));

        // Navigation bar setup
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

        // Scrollable content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(171, 139, 122));

        // Item panel setup
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(new Color(171, 139, 122));

        // Create the JScrollPane for items
        JScrollPane itemScrollPane = new JScrollPane(itemPanel);
        itemScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        itemScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove the border
        itemScrollPane.setBackground(new Color(171, 139, 122)); // Match background color

        contentPanel.add(itemScrollPane);

        // Load category items from the database
        loadCategoryItems(itemPanel);

        // Add space before footer
        contentPanel.add(Box.createVerticalStrut(30));
        addFooter(contentPanel);

        // Create the main scroll pane for the content panel
        JScrollPane contentScrollPane = new JScrollPane(contentPanel);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove the border
        contentScrollPane.setBackground(new Color(171, 139, 122)); // Match background color

        // Add mouse wheel listener to itemPanel for scrolling
        itemPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar vertical = itemScrollPane.getVerticalScrollBar();
                int notches = e.getWheelRotation(); // negative means up
                vertical.setValue(vertical.getValue() + notches * vertical.getUnitIncrement(notches > 0 ? 1 : -1));
            }
        });

        // Add the content scroll pane to the main panel
        mainPanel.add(contentScrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadCategoryItems(JPanel itemPanel) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "SELECT id, name, image_path, price FROM " + category.toLowerCase();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("id");
                String name = rs.getString("name");
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");

                JPanel item = new JPanel(new BorderLayout());
                item.setBackground(new Color(171, 139, 122)); // Match background with HomePage

                JLabel imageLabel = new JLabel(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                item.add(imageLabel, BorderLayout.WEST);

                JPanel details = new JPanel(new GridLayout(3, 1));
                details.setBackground(new Color(171, 139, 122)); // Match background
                details.add(new JLabel("Name: " + name));
                details.add(new JLabel("Price: $" + price));

                JButton addToCartButton = new JButton("Add to Cart");
                JButton addToFavouritesButton = new JButton("Add to Favourites");

                addToCartButton.addActionListener(e -> addToCart(itemId));
                addToFavouritesButton.addActionListener(e -> addToFavourites(itemId));

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(171, 139, 122)); // Match background
                buttonPanel.add(addToCartButton);
                buttonPanel.add(addToFavouritesButton);

                details.add(buttonPanel);
                item.add(details, BorderLayout.CENTER);

                itemPanel.add(item);
            }

            // Refresh the item panel to show newly added items
            itemPanel.revalidate();
            itemPanel.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while retrieving " + category + " items.");
        }
    }

    private void addToCart(int itemId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String checkQuery = "SELECT quantity FROM cart WHERE user_id = ? AND item_id = ? AND category = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, itemId);
            checkStmt.setString(3, category);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int newQuantity = rs.getInt("quantity") + 1;
                String updateQuery = "UPDATE cart SET quantity = ? WHERE user_id = ? AND item_id = ? AND category = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, itemId);
                updateStmt.setString(4, category);
                updateStmt.executeUpdate();
            } else {
                String insertQuery = "INSERT INTO cart (user_id, item_id, category, quantity) VALUES (?, ?, ?, 1)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, itemId);
                insertStmt.setString(3, category);
                insertStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Item added to cart.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while adding item to cart.");
        }
    }

    private void addToFavourites(int itemId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "")) {
            String query = "INSERT IGNORE INTO favourites (user_id, item_id, category) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.setString(3, category);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added to favourites.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while adding item to favourites.");
        }
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
                break;
            case "Orders":
                new OrdersPage(userId).setVisible(true);
                break;
            case "Favourites":
                new FavoritePage(userId).setVisible(true);
                break;
            case "Cart":
                new CartPage(userId).setVisible(true);
                break;
            case "Profile":
                new ProfilePage(userId).setVisible(true);
                break;
            case "History":
                new HistoryPage(userId).setVisible(true);
                break;
            case "Logout":
                int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginForm().setVisible(true);
                }
                break;
        }
        dispose();
    }

    private void addFooter(JPanel contentPanel) {
        // Footer setup
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(new Color(171, 139, 122));

        // Title for the footer
        JLabel footerTitle = new JLabel("ABOUT");
        footerTitle.setFont(new Font("Serif", Font.BOLD, 70));
        footerTitle.setForeground(Color.WHITE);
        footerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Paragraph content
        String aboutText = "<html><center>Welcome to SAJEF Restaurant, <br>where culinary artistry meets heartwarming hospitality!<br>" +
                "Nestled in the heart of the city, our restaurant is a haven for <br> food enthusiasts who seek a delightful escape from the ordinary.<br>" +
                "At SAJEF, we believe in the magic of flavors and <br>the joy of sharing exquisite meals with loved ones.</center></html>";
        JLabel footerContent = new JLabel(aboutText);
        footerContent.setFont(new Font("Arial", Font.PLAIN, 30));
        footerContent.setForeground(Color.WHITE);
        footerContent.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerContent.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text

        // Add title and content to footer panel
        footerPanel.add(footerTitle);
        footerPanel.add(Box.createVerticalStrut(10)); // Space between title and content
        footerPanel.add(footerContent);

        contentPanel.add(footerPanel);
        contentPanel.add(Box.createVerticalStrut(80)); // Space after footer
    }

    public static void main(String[] args) {
        // For testing purposes, passing a dummy user ID
        new CategoryPage(1, "Pizza").setVisible(true);
    }
}