package com.bookmanagementsystem.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.bookmanagementsystem.model.User;
import com.bookmanagementsystem.util.AppPaths;

public class MyGUI extends JFrame {
    private static final Path DATABASE_FILE = AppPaths.dataFile("users.csv");
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private Map<String, User> usersMap;

    public MyGUI() {
        setTitle("Login / Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(400, 250);

        usersMap = loadUsersFromCSV();
        usersMap.put(ADMIN_USERNAME, new User(ADMIN_USERNAME, ADMIN_PASSWORD, true));

        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(Color.PINK);
        setContentPane(contentPane);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridwidth = GridBagConstraints.REMAINDER;
        gbcCenter.insets = new Insets(10, 10, 10, 10);
        gbcCenter.anchor = GridBagConstraints.CENTER;

        JLabel registrationLabel = new JLabel("Registration");
        registrationLabel.setFont(new Font("Arial", Font.BOLD, 28));
        registrationLabel.setForeground(Color.BLACK);
        contentPane.add(registrationLabel, gbcCenter);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.anchor = GridBagConstraints.EAST;
        contentPane.add(usernameLabel, gbcUsernameLabel);
        contentPane.add(usernameField, gbcCenter);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.anchor = GridBagConstraints.EAST;
        contentPane.add(passwordLabel, gbcPasswordLabel);
        contentPane.add(passwordField, gbcCenter);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridwidth = GridBagConstraints.REMAINDER;
        gbcButtons.insets = new Insets(10, 10, 10, 10);
        gbcButtons.anchor = GridBagConstraints.CENTER;

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 128, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (usersMap.containsKey(username)) {
                User user = usersMap.get(username);
                if (user.verifyPassword(password)) {
                    if (user.isAdmin()) {
                        new GeneralDatabaseOfAdmin().setVisible(true);
                    } else {
                        new GeneralDatabaseGUI(username).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(MyGUI.this,
                            "Invalid username or password. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MyGUI.this,
                        "Invalid username or password. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(MyGUI.this,
                        "Please enter both username and password.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usersMap.containsKey(username)) {
                JOptionPane.showMessageDialog(MyGUI.this,
                        "Username already exists. Please choose a different username.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                if (!isPasswordStrong(password)) {
                    JOptionPane.showMessageDialog(MyGUI.this,
                            "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User newUser = new User(username, password, false);
                usersMap.put(username, newUser);
                saveUsersToCSV();
                JOptionPane.showMessageDialog(MyGUI.this,
                        "Registration successful. You can now login.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                usernameField.setText("");
                passwordField.setText("");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        contentPane.add(buttonPanel, gbcButtons);

        setLocationRelativeTo(null);
    }

    private Map<String, User> loadUsersFromCSV() {
        Map<String, User> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(DATABASE_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    boolean isAdmin = Boolean.parseBoolean(parts[2]);
                    map.put(parts[0], new User(parts[0], parts[1], isAdmin));
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "User database file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return map;
    }

    private void saveUsersToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(DATABASE_FILE)) {
            for (Map.Entry<String, User> entry : usersMap.entrySet()) {
                User user = entry.getValue();
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.isAdmin());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyGUI().setVisible(true));
    }
}
