import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/** Main class for the Admin panel managing general database, users, and user reviews */
public class GeneralDatabaseOfAdmin extends JFrame {
    private DefaultTableModel tableModel;
    private DefaultTableModel titlesTableModel;
    private DefaultTableModel reviewsTableModel;
    private ArrayList<String[]> allBooks;
    private JTextField searchField;
    private ArrayList<String[]> allUsers;
    private ArrayList<String[]> allReviews;

    /** Constructor to initialize the GUI components */
    public GeneralDatabaseOfAdmin() {
        setTitle("Admin Panel");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.PINK);

        JPanel titlesPanel = new JPanel(new BorderLayout());
        titlesPanel.setBackground(Color.PINK);
        tabbedPane.addTab("Books", titlesPanel);
        createTitlesPanel(titlesPanel);

        // Users tab
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBackground(Color.PINK);
        tabbedPane.addTab("Users", usersPanel);
        createUsersPanel(usersPanel);

        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBackground(Color.PINK);
        tabbedPane.addTab("User Reviews", reviewsPanel);
        createUserReviewsPanel(reviewsPanel);

        add(tabbedPane);
        setVisible(true);

        // Header panel with BorderLayout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.PINK); // Set background color to pink

        // Header label
        JLabel headerLabel = new JLabel("Book Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 31));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Back button panel with FlowLayout
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(Color.PINK); // Set background color to pink

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            MyGUI loginPage = new MyGUI();
            loginPage.setVisible(true);
            dispose();
        });
        backButton.setBackground(new Color(0,128,255));
        backButton.setForeground(Color.WHITE);
        backButtonPanel.add(backButton);

        // Add header panel and back button panel to NORTH of the frame
        headerPanel.add(backButtonPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Search panel with FlowLayout
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.PINK); // Set background color to pink

        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter title or author to search");
        searchPanel.add(searchField);

        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(34, 139, 34)); // Dark green
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            filterBooks(query);
        });

        searchPanel.add(searchButton);

        // Add search panel to NORTH of the frame
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Initialize table model
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Boolean.class;
                } else {
                    return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
    }

    /** Create panel for Users tab */
    private void createUsersPanel(JPanel usersPanel) {
        // Create list to display users
        JList<String> userList = new JList<>();
        loadUsers(userList);
        JScrollPane scrollPane = new JScrollPane(userList);
        usersPanel.add(scrollPane, BorderLayout.CENTER);

        // Button to delete selected user
        JButton deleteButton = new JButton("Delete Selected User");
        deleteButton.addActionListener(e -> deleteUser(userList.getSelectedValue()));
        usersPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    /** Load users into the JList */
    private void loadUsers(JList<String> userList) {
        allUsers = new ArrayList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        // Load users from CSV file
        try (BufferedReader br = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                allUsers.add(data);
                model.addElement(data[0]); // Add username to the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        userList.setModel(model);
    }

    /** Delete selected user */
    @SuppressWarnings("unchecked")
    private void deleteUser(String selectedUser) {
        for (String[] user : allUsers) {
            if (user[0].equals(selectedUser)) {
                allUsers.remove(user);
                break;
            }
        }
        saveUsersToCSV();
        loadUsers((JList<String>) ((JScrollPane) ((JPanel) ((JButton) ((Component) (this)).getParent()).getParent()).getComponent(0)).getViewport().getView());
    }

    /** Save users to CSV */
    private void saveUsersToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv"))) {
            for (String[] user : allUsers) {
                writer.write(String.join(",", user) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Create panel for Titles tab */
    private void createTitlesPanel(JPanel titlesPanel) {
        titlesTableModel = new DefaultTableModel(new Object[]{"Title", "Author"}, 0);
        JTable table = new JTable(titlesTableModel);
        table.setDefaultRenderer(Object.class, new CustomRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        titlesPanel.add(scrollPane, BorderLayout.CENTER);

        loadTitles();
        JButton deleteButton = new JButton("Delete Selected Books");
        deleteButton.addActionListener(e -> deleteSelectedTitles(table.getSelectedRows()));
        titlesPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    /** Method to delete selected titles */
    private void deleteSelectedTitles(int[] selectedRows) {
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int selectedRow = selectedRows[i];
            titlesTableModel.removeRow(selectedRow);
            allBooks.remove(selectedRow); // Remove corresponding data from the list
        }
        saveTitlesToCSV(); // Update CSV file with modified data
    }

    /** Method to save titles to CSV */
    private void saveTitlesToCSV() {
        String csvFile = "brodsky.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            for (String[] book : allBooks) {
                writer.write(String.join(",", book) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving titles to CSV file.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Load titles from CSV file */
    private void loadTitles() {
        allBooks = new ArrayList<>();
        String csvFile = "brodsky.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String title = data[0].trim().isEmpty() ? "Unknown" : data[0].trim();
                    String author = data[1].trim();
                    allBooks.add(new String[]{title, author});
                    titlesTableModel.addRow(new Object[]{title, author, false});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from CSV file.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Create panel for User Reviews tab */
    private void createUserReviewsPanel(JPanel reviewsPanel) {
        reviewsTableModel = new DefaultTableModel(new Object[]{"Title", "Author", "Rating", "Reviews"}, 0);
        JTable table = new JTable(reviewsTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        reviewsPanel.add(scrollPane, BorderLayout.CENTER);

        loadReviews();
        JButton deleteButton = new JButton("Delete Selected Reviews");
        deleteButton.addActionListener(e -> deleteSelectedReviews(table.getSelectedRows()));
        reviewsPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    /** Load user reviews from CSV file */
    private void loadReviews() {
        allReviews = new ArrayList<>();
        String csvFile = "General.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    String title = data[0].trim();
                    String author = data[1].trim();
                    String rating = data[2].trim();
                    String reviews = data[3].trim();
                    allReviews.add(new String[]{title, author, rating, reviews});
                    reviewsTableModel.addRow(new Object[]{title, author, rating, reviews});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from CSV file.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Method to delete selected reviews */
    private void deleteSelectedReviews(int[] selectedRows) {
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int selectedRow = selectedRows[i];
            reviewsTableModel.removeRow(selectedRow);
            allReviews.remove(selectedRow); // Remove corresponding data from the list
        }
        saveReviewsToCSV(); // Update CSV file with modified data
    }

    /** Method to save reviews to CSV */
    private void saveReviewsToCSV() {
        String csvFile = "General.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            for (String[] review : allReviews) {
                writer.write(String.join(",", review) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving reviews to CSV file.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Main method to launch the application */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GeneralDatabaseOfAdmin::new);
    }

    /** Filter books based on the search query */
    private void filterBooks(String query) {
        if (query.isEmpty()) {
            // Show all books if search query is empty
            showAllBooks();
            return;
        }

        // Filter books based on the search query
        ArrayList<String[]> filteredBooks = new ArrayList<>();
        for (String[] book : allBooks) {
            String title = book[0].toLowerCase();
            String author = book[1].toLowerCase();
            if (title.contains(query) || author.contains(query)) {
                filteredBooks.add(book);
            }
        }

        // Update the table with filtered books
        updateTable(filteredBooks);
    }

    /** Show all books in the table */
    private void showAllBooks() {
        updateTable(allBooks);
    }

    /** Update the table with the provided list of books */
    private void updateTable(ArrayList<String[]> books) {
        tableModel.setRowCount(0); // Clear the table
        for (String[] book : books) {
            tableModel.addRow(new Object[]{book[0], book[1], "No rating", "No reviews", false});
        }
    }

    /** Custom cell renderer for setting pink background color */
    private class CustomRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(Color.PINK);
            return c;
        }
    }
}
