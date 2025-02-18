import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {
    private int userId;
    private JPanel bannerPanel;
    private int currentImageIndex = 0;
    private final String[] bannerImages = {
        "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\bg.jpg",
        "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\bg5.jpg",
        "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\bg4.jpg",
        "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\bg3.jpg"
    };

    public HomePage(int userId) {
        this.userId = userId;

        setTitle("Home Page");
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

        // Banner image setup (using CardLayout for sliding effect)
        bannerPanel = new JPanel(new CardLayout());
        bannerPanel.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 750));
        for (String imagePath : bannerImages) {
            ImageIcon icon = new ImageIcon(
                new ImageIcon(imagePath).getImage().getScaledInstance(Toolkit.getDefaultToolkit().getScreenSize().width, 750, Image.SCALE_SMOOTH)
            );
            JLabel imageLabel = new JLabel(icon);
            bannerPanel.add(imageLabel);
        }
        contentPanel.add(bannerPanel);

        // Timer to change banner image every 5 seconds with sliding effect
        Timer timer = new Timer(5000, e -> {
            CardLayout cl = (CardLayout) (bannerPanel.getLayout());
            cl.next(bannerPanel);  // Transition to next image
        });
        timer.start();

        // Add space between banner and menu
        contentPanel.add(Box.createVerticalStrut(30));

        // Centered menu label and menu name using GridBagLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Centering the menuLabel in the grid
        JLabel menuLabel = new JLabel("<html><center>crack, cook, enjoy</center></html>");
        menuLabel.setFont(new Font("Script Bold Italic", Font.BOLD | Font.ITALIC, 100));
        menuLabel.setForeground(Color.DARK_GRAY);
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        centerPanel.add(menuLabel, gbc);

        // Centering the menuName in the grid
        JLabel menuName = new JLabel("<html>MENU</html>");
        menuName.setFont(new Font("Serif", Font.BOLD, 70));  // Changed font and size
        menuName.setForeground(Color.white);
        menuName.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(menuName, gbc);

        contentPanel.add(centerPanel);

        // Centered menu panel with GridBagLayout for spacing
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints menuGbc = new GridBagConstraints();
        menuGbc.insets = new Insets(50, 50, 50, 50);

        String[] menuItems = {"Burger", "Starch", "Pizza", "Biryani", "Pasta", "Shawarma", "Ice Cream", "Pav", "Soup", "Cake"};
        String[] imagePaths = {
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\bug01.jpg",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\starch.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\pizza.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\biryani.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\pasta.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\shawarma.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\icecream.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\vadapav.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\soup.png",
            "C:\\Users\\Hp\\Desktop\\Sem5\\FoodApp\\src\\images\\cakes.png"
        };

        for (int i = 0; i < menuItems.length; i++) {
            String menuItem = menuItems[i];
            ImageIcon icon = new ImageIcon(new ImageIcon(imagePaths[i]).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));

            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setOpaque(false);

            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    displayCategoryItems(menuItem);
                }
            });

            JLabel nameLabel = new JLabel(menuItem, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setForeground(Color.DARK_GRAY);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            itemPanel.add(imageLabel);
            itemPanel.add(nameLabel);

            menuGbc.gridx = i % 3;
            menuGbc.gridy = i / 3;
            menuPanel.add(itemPanel, menuGbc);
        }

        contentPanel.add(menuPanel);

        // Add ABOUT footer
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
                "Nestled in the heart of the city, our restaurant is a haven for <br> food enthusiasts who seek a </br> delightful escape from the ordinary.<br>" +
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

        contentPanel.add(Box.createVerticalStrut(30)); // Space before footer
        contentPanel.add(footerPanel);	

        contentPanel.add(Box.createVerticalStrut(80)); // Space before footer
        contentPanel.add(footerPanel);	
        contentPanel.add(Box.createVerticalStrut(100)); // Space after footer
        
        JScrollPane contentScrollPane = new JScrollPane(contentPanel);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(contentScrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void addNavigationText(JPanel navPanel, boolean isLogout) {
        String[] buttonNames = isLogout ? new String[]{"Logout"} : new String[]{"Home", "Contact", "Favourites", "Cart", "Profile", "History"};
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

    private void displayCategoryItems(String category) {
        new CategoryPage(userId, category).setVisible(true);
        dispose();
    }

    private void handleNavigation(String name) {
        switch (name) {
            case "Home":
                new HomePage(userId).setVisible(true);
                break;
            case "Contact":
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
                new LoginForm().setVisible(true);
                break;
        }
        dispose();
    }

    public static void main(String[] args) {
        // For testing purposes, passing a dummy user ID
        new HomePage(1).setVisible(true);
    }
}