import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserForm extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JButton submitButton;

    public UserForm() {
        setTitle("User Information Form");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        add(nameLabel);
        add(nameField);
        add(addressLabel);
        add(addressField);
        add(new JLabel()); // empty cell
        add(submitButton);
    }

    private void saveData() {
        String name = nameField.getText();
        String address = addressField.getText();

        String url = "jdbc:mysql://172.20.10.2:3306/user_data";
        String user = "sotbard"; // replace with your DB username
        String password = "sotbard"; // replace with your DB password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO users (name, address) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, address);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data saved successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserForm form = new UserForm();
            form.setVisible(true);
        });
    }
}