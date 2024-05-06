import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyGUI extends JFrame {
    private static final String DATABASE_FILE = "users.csv";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private Map<String, User> usersMap; // Map to store usernames and User objects

    public MyGUI() {
        setTitle("Login / Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(400, 250); // Set the size of the frame

        // Load existing users from CSV database into memory
        usersMap = loadUsersFromCSV();

        // Add admin credentials to the map
        usersMap.put(ADMIN_USERNAME, new User(ADMIN_USERNAME, ADMIN_PASSWORD, true));

        // Create a panel with GridBagLayout
        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(Color.PINK); // Set the background color to pink
        setContentPane(contentPane);

        // Create GridBagConstraints for center alignment
        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridwidth = GridBagConstraints.REMAINDER;
        gbcCenter.insets = new Insets(10, 10, 10, 10);
        gbcCenter.anchor = GridBagConstraints.CENTER;

        // Registration heading
        JLabel registrationLabel = new JLabel("Registration");
        registrationLabel.setFont(new Font("Arial", Font.BOLD, 28));
        registrationLabel.setForeground(Color.BLACK); // Change color to black
        contentPane.add(registrationLabel, gbcCenter);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30)); // Set preferred size
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.anchor = GridBagConstraints.EAST;
        contentPane.add(usernameLabel, gbcUsernameLabel);
        contentPane.add(usernameField, gbcCenter);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30)); // Set preferred size
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.anchor = GridBagConstraints.EAST;
        contentPane.add(passwordLabel, gbcPasswordLabel);
        contentPane.add(passwordField, gbcCenter);

        // Create GridBagConstraints for button alignment
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridwidth = GridBagConstraints.REMAINDER;
        gbcButtons.insets = new Insets(10, 10, 10, 10);
        gbcButtons.anchor = GridBagConstraints.CENTER;

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 128, 255)); // Dark blue
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate login credentials
                if (usersMap.containsKey(username)) {
                    User user = usersMap.get(username);
                    if (user.verifyPassword(password)) {
                        // Show welcome message and open the main page
                        new TransitionPage(user.isAdmin());
                        // Close the login/registration page
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(MyGUI.this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(MyGUI.this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(34, 139, 34)); // Dark green
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate input fields
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(MyGUI.this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if username already exists
                if (usersMap.containsKey(username)) {
                    JOptionPane.showMessageDialog(MyGUI.this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Check password strength
                    if (!isPasswordStrong(password)) {
                        JOptionPane.showMessageDialog(MyGUI.this,
                                "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Add new user to the map and update CSV file
                    User newUser = new User(username, password, false);
                    usersMap.put(username, newUser);
                    saveUsersToCSV();
                    JOptionPane.showMessageDialog(MyGUI.this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear username and password fields after successful registration
                    usernameField.setText("");
                    passwordField.setText("");
                }
            }
        });

        // Add login and register buttons in the same line
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        contentPane.add(buttonPanel, gbcButtons);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private Map<String, User> loadUsersFromCSV() {
        Map<String, User> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            // Read CSV file line by line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    // Store username, password, and admin status in map
                    boolean isAdmin = Boolean.parseBoolean(parts[2]);
                    map.put(parts[0], new User(parts[0], parts[1], isAdmin));
                }
            }
        } catch (FileNotFoundException e) {
            // Handle file not found error
            JOptionPane.showMessageDialog(this, "User database file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // Handle other IO errors
            JOptionPane.showMessageDialog(this, "Error reading user database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return map;
    }

    private void saveUsersToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            // Write usersMap to CSV file
            for (Map.Entry<String, User> entry : usersMap.entrySet()) {
                User user = entry.getValue();
                bw.write(user.getUsername() + "," + user.getPassword() + "," + user.isAdmin());
                bw.newLine();
            }
        } catch (IOException e) {
            // Handle IO errors during file writing
            JOptionPane.showMessageDialog(this, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPasswordStrong(String password) {
        // Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyGUI().setVisible(true);
            }
        });
    }
}
