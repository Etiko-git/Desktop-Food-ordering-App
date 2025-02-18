import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FooterPanel extends JPanel { // Extend JPanel
    public FooterPanel() {
        setLayout(new GridLayout(1, 4)); // One row, four columns

        // Add each section to the footer
        add(createSection("Company", new String[]{"About", "Team", "Career"}, new String[]{"about.png", "team.png", "career.png"}));
        add(createSection("Contact", new String[]{"Help", "Support"}, new String[]{"help.png", "support.png"}));
        add(createSection("Legal", new String[]{"Terms and Conditions"}, new String[]{"terms.png"}));
        add(createSection("Social Media", new String[]{"Instagram", "Facebook", "GitHub"}, new String[]{"instagram.png", "facebook.png", "github.png"}));
    }

    private JPanel createSection(String title, String[] links, String[] icons) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS)); // Vertical layout for section

        // Title for the section
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Bold title
        sectionPanel.add(titleLabel);

        // Create a panel for links and icons
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        for (int i = 0; i < links.length; i++) {
            JButton linkButton = new JButton(links[i]);
            linkButton.setIcon(new ImageIcon(icons[i])); // Load icon for button
            linkButton.setBorderPainted(false);
            linkButton.setFocusPainted(false);
            linkButton.setContentAreaFilled(false);

            // Action listener for each button
            linkButton.addActionListener(createLinkListener(links[i]));
            linksPanel.add(linkButton);
        }

        sectionPanel.add(linksPanel); // Add links panel below the title
        return sectionPanel;
    }

    private ActionListener createLinkListener(String link) {
        return e -> {
            // Handle the navigation here
            JOptionPane.showMessageDialog(this, link + " clicked!");
        };
    }
}