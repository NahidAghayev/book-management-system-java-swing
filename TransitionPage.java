import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransitionPage extends JFrame {
    private boolean isAdmin;

    public TransitionPage(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("Book Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.PINK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Align elements to the left
        gbc.insets = new Insets(10, 10, 10, 10); // Adjusted insets for title and button

        JLabel titleLabel = new JLabel("Welcome to the Book Management System!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, gbc);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame
                new MyGUI().setVisible(true); // Open the login/registration form
            }
        });

        // Updated constraints for the Back button
        gbc.gridx = 0; // Align with the left part of the title
        gbc.gridy++; // Move to the next row
        gbc.anchor = GridBagConstraints.WEST; // Align the Back button to the left
        gbc.weightx = 0; // Reset the weight
        mainPanel.add(backButton, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generalDatabaseButton = new JButton("General Database");
        generalDatabaseButton.setBackground(new Color(0, 128, 255)); // Dark blue
        generalDatabaseButton.setForeground(Color.WHITE);
        generalDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame
                new GeneralDatabaseGUI(isAdmin).setVisible(true); // Open the General Database GUI
            }
        });
        buttonPanel.add(generalDatabaseButton);

        if (!isAdmin) { // Only non-admin users can access personal database
            JButton personalDatabaseButton = new JButton("Personal Database");
            personalDatabaseButton.setBackground(new Color(34, 139, 34)); // Dark green
            personalDatabaseButton.setForeground(Color.WHITE);
            personalDatabaseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the current frame
                    new PersonalDatabaseGUI().setVisible(true); // Open the Personal Database GUI
                }
            });
            buttonPanel.add(personalDatabaseButton);
        }

        gbc.gridx = 0; // Align with the left part of the title
        gbc.gridy++; // Move to the next row
        gbc.weightx = 0; // Reset the weight
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Test the TransitionPage class
        new TransitionPage(false); // Assuming non-admin user
    }
}
