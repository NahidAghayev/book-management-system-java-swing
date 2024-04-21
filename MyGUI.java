import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyGUI extends JFrame {
    private static final String DATABASE_FILE = "users.csv";
    private Map<String, String> usersMap; // Map to store usernames and passwords

    public MyGUI() {
        setTitle("Login / Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 250); // Set the size of the frame

        // Load existing users from CSV database into memory
        usersMap = loadUsersFromCSV();

        // Create a panel for login/registration with colorful background
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10)); // Increase the number of rows to accommodate more components
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        // Login button with colorful appearance
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.BLUE); // Set button color
        loginButton.setForeground(Color.WHITE); // Set text color
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate login credentials
                if (usersMap.containsKey(username) && usersMap.get(username).equals(password)) {
                    JOptionPane.showMessageDialog(MyGUI.this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MyGUI.this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton);

        // Register button with colorful appearance
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.GREEN); // Set button color
        registerButton.setForeground(Color.WHITE); // Set text color
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
                    usersMap.put(username, password);
                    saveUsersToCSV();
                    JOptionPane.showMessageDialog(MyGUI.this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear username and password fields after successful registration
                    usernameField.setText("");
                    passwordField.setText("");
                }
            }
        });
        panel.add(registerButton);

        add(panel);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private Map<String, String> loadUsersFromCSV() {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            // Read CSV file line by line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    // Store username and password in map
                    map.put(parts[0], parts[1]);
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
            for (Map.Entry<String, String> entry : usersMap.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
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
