package com.bookmanagementsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bookmanagementsystem.util.AppPaths;
import com.bookmanagementsystem.util.FilterAndSortFunction;

public class GeneralDatabaseGUI extends JFrame {
    private static final Path BOOKS_FILE = AppPaths.dataFile("copy.csv");
    private static final Path PERSONAL_FILE = AppPaths.dataFile("personal.csv");
    private static final Path GENERAL_FILE = AppPaths.dataFile("general.csv");

    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ArrayList<String[]> allBooks;
    private final ArrayList<String[]> selectedBooks = new ArrayList<>();

    public GeneralDatabaseGUI(String username) {
        setTitle("Book Management System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Book Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(Color.PINK);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            MyGUI loginPage = new MyGUI();
            loginPage.setVisible(true);
            dispose();
        });
        backButton.setBackground(new Color(0, 128, 255));
        backButton.setForeground(Color.WHITE);
        backButtonPanel.add(backButton);

        headerPanel.add(backButtonPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter title or author to search");
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(34, 139, 34));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            filterBooks(query);
        });
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Reviews");
        tableModel.addColumn("Selected");

        readDataFromCSV();

        JTable bookTable = new JTable(tableModel);
        bookTable.setRowHeight(30);
        bookTable.setGridColor(Color.PINK);

        JTableHeader tableHeader = bookTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        bookTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        bookTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                component.setBackground(row % 2 == 0 ? new Color(255, 230, 230) : new Color(255, 204, 204));
                return component;
            }
        });

        bookTable.getColumnModel().getColumn(3).setCellRenderer(new ReviewCellRenderer());
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = bookTable.getColumnModel().getColumnIndex("Reviews");
                int row = bookTable.rowAtPoint(e.getPoint());

                if (column == 3 && row != -1) {
                    Rectangle cellRect = bookTable.getCellRect(row, column, false);
                    if (cellRect.contains(e.getPoint())) {
                        String reviews = (String) bookTable.getValueAt(row, column);
                        if (!reviews.equals("No reviews")) {
                            String[] users = reviews.split(", ");
                            String selectedUser = (String) JOptionPane.showInputDialog(
                                    GeneralDatabaseGUI.this,
                                    "Select a user to view details:",
                                    "User Details",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    users,
                                    null);

                            if (selectedUser != null && !selectedUser.isEmpty()) {
                                String title = (String) bookTable.getValueAt(row, 0);
                                String author = (String) bookTable.getValueAt(row, 1);
                                String averageRating = (String) bookTable.getValueAt(row, 2);
                                String userRating = getUserRatingForBook(selectedUser, title);
                                String userReview = getUserReviewForBook(selectedUser, title);
                                showUserDetails(title, author, averageRating, selectedUser, userRating, userReview);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton addToLibraryButton = new JButton("Add to Personal Library");
        addToLibraryButton.addActionListener(e -> addToLibrary(username));
        add(addToLibraryButton, BorderLayout.SOUTH);

        FilterAndSortFunction.sortSelected(bookTable);
        setVisible(true);

        calculateAverageRatings();
        saveDataToGeneralCSV();
    }

    private void readDataFromCSV() {
        allBooks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(BOOKS_FILE), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 1) {
                    String title = data[0].trim();
                    String author;

                    if (title.startsWith("\"") && title.endsWith("\"")) {
                        title = title.substring(1, title.length() - 1);
                    }

                    title = title.replaceAll("[^\\p{Print}]", "");
                    if (title.isEmpty()) {
                        title = "Unknown";
                    }

                    if (data.length > 1) {
                        author = data[1].trim();
                    } else {
                        author = "Unknown";
                    }

                    String[] titles = title.split(",\\s*");
                    String[] authors = author.split(",\\s*");

                    for (String bookTitle : titles) {
                        for (String authorName : authors) {
                            allBooks.add(new String[] { bookTitle.trim(), authorName.trim() });
                            String averageRating = calculateAverageRatingForBook(bookTitle);
                            String rating = averageRating.equals("No Rating") ? "No Rating" : averageRating;
                            String reviews = loadReviewsForBook(bookTitle);
                            tableModel.addRow(new Object[] { bookTitle, authorName, rating, reviews, false });
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String loadReviewsForBook(String title) {
        StringBuilder reviews = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            boolean hasReviews = false;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 11 && data[1].trim().equals(title) && !data[10].trim().isEmpty()) {
                    if (reviews.length() > 0) {
                        reviews.append(", ");
                    }
                    reviews.append(data[0].trim());
                    hasReviews = true;
                }
            }
            if (!hasReviews) {
                return "No reviews";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reviews.toString();
    }

    private String calculateAverageRatingForBook(String title) {
        ArrayList<String[]> userRatings = loadUserRatings();

        double totalRating = 0.0;
        int count = 0;
        boolean hasRating = false;
        for (String[] rating : userRatings) {
            if (title.equals(rating[0]) && !rating[1].isEmpty()) {
                try {
                    totalRating += Double.parseDouble(rating[1]);
                    count++;
                    hasRating = true;
                } catch (NumberFormatException e) {
                    // Ignore invalid ratings.
                }
            }
        }
        if (hasRating) {
            double averageRating = totalRating / count;
            return String.format("%.2f (%d)", averageRating, count);
        }
        return "No Rating";
    }

    private class ReviewCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText("<html><u>" + value + "</u></html>");
            return label;
        }
    }

    private String getUserRatingForBook(String username, String title) {
        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 11 && data[0].equals(username) && data[1].trim().equals(title)) {
                    return data[9].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No rating";
    }

    private String getUserReviewForBook(String username, String title) {
        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 11 && data[0].equals(username) && data[1].trim().equals(title)) {
                    return data[10].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No review";
    }

    private void showUserDetails(String title, String author, String averageRating,
            String username, String userRating, String userReview) {
        JFrame userDetailsFrame = new JFrame("User Details");
        userDetailsFrame.setSize(400, 200);
        userDetailsFrame.setLocationRelativeTo(null);
        userDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel userDetailsPanel = new JPanel();
        userDetailsPanel.setLayout(new BoxLayout(userDetailsPanel, BoxLayout.Y_AXIS));
        userDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 4, 3, 4));

        JLabel titleLabel = new JLabel("Title: " + title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel authorLabel = new JLabel("Author: " + author);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel ratingLabel = new JLabel("Average Rating: " + averageRating);
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 2));

        JLabel userLabel = new JLabel("User: " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel userRatingLabel = new JLabel("User Rating: " + userRating);
        userRatingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel userReviewLabel = new JLabel("User Review: " + userReview);
        userReviewLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        userDetailsPanel.add(titleLabel);
        userDetailsPanel.add(authorLabel);
        userDetailsPanel.add(ratingLabel);
        userDetailsPanel.add(Box.createVerticalStrut(10));
        userDetailsPanel.add(separator);
        userDetailsPanel.add(Box.createVerticalStrut(10));
        userDetailsPanel.add(userLabel);
        userDetailsPanel.add(userRatingLabel);
        userDetailsPanel.add(userReviewLabel);

        userDetailsFrame.add(userDetailsPanel);
        userDetailsFrame.setVisible(true);
    }

    private void calculateAverageRatings() {
        ArrayList<String[]> userRatings = loadUserRatings();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String title = (String) tableModel.getValueAt(row, 0);

            double totalRating = 0.0;
            int count = 0;
            boolean hasRating = false;
            for (String[] rating : userRatings) {
                if (title.equals(rating[0]) && !rating[1].isEmpty()) {
                    try {
                        totalRating += Double.parseDouble(rating[1]);
                        count++;
                        hasRating = true;
                    } catch (NumberFormatException e) {
                        // Ignore invalid ratings.
                    }
                }
            }
            if (hasRating) {
                double averageRating = totalRating / count;
                tableModel.setValueAt(String.format("%.2f (%d)", averageRating, count), row, 2);
            } else {
                tableModel.setValueAt("No Rating", row, 2);
            }
        }
    }

    private ArrayList<String[]> loadUserRatings() {
        ArrayList<String[]> userRatings = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 11) {
                    String title = data[1].trim();
                    String rating = data[9].trim();
                    userRatings.add(new String[] { title, rating });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userRatings;
    }

    private void saveDataToGeneralCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(GENERAL_FILE)) {
            writer.write("Title,Author,Rating,Reviews");
            writer.newLine();

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                StringBuilder rowData = new StringBuilder();
                for (int col = 0; col < tableModel.getColumnCount() - 1; col++) {
                    if (col == 2) {
                        String title = (String) tableModel.getValueAt(row, 0);
                        String rating = calculateAverageRatingForBook(title);
                        rowData.append(rating);
                        updateRatingInPersonalCSV(title, rating);
                    } else {
                        rowData.append(tableModel.getValueAt(row, col));
                    }
                    if (col < tableModel.getColumnCount() - 2) {
                        rowData.append(",");
                    }
                }
                writer.write(rowData.toString());
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateRatingInPersonalCSV(String title, String rating) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");
            if (data.length >= 4 && data[1].equals(title)) {
                data[3] = rating;
                lines.set(i, String.join(",", data));
                break;
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(PERSONAL_FILE)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToLibrary(String username) {
        selectedBooks.clear();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            boolean isSelected = (boolean) tableModel.getValueAt(row, 4);
            if (isSelected) {
                String[] selectedBook = new String[5];
                selectedBook[0] = (String) tableModel.getValueAt(row, 0);
                selectedBook[1] = (String) tableModel.getValueAt(row, 1);
                selectedBook[2] = (String) tableModel.getValueAt(row, 2);
                selectedBook[3] = (String) tableModel.getValueAt(row, 3);
                selectedBook[4] = "";
                selectedBooks.add(selectedBook);
            }
        }

        if (!selectedBooks.isEmpty()) {
            ArrayList<String[]> userBookHistory = loadUserBookHistory(username);

            ArrayList<String[]> booksToAdd = new ArrayList<>();
            boolean anyAlreadyExist = false;
            for (String[] selectedBook : selectedBooks) {
                boolean alreadyExists = false;
                for (String[] history : userBookHistory) {
                    if (selectedBook[0].equals(history[0]) && selectedBook[1].equals(history[1])) {
                        alreadyExists = true;
                        anyAlreadyExist = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    booksToAdd.add(selectedBook);
                }
            }

            if (!booksToAdd.isEmpty()) {
                new PersonalDatabaseGUI(booksToAdd, userBookHistory, username);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Selected books already exist in your library.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                if (!anyAlreadyExist) {
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        tableModel.setValueAt(false, row, 4);
                    }
                }
            }
        }
    }

    private ArrayList<String[]> loadUserBookHistory(String username) {
        ArrayList<String[]> userBookHistory = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(PERSONAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username)) {
                    String[] userData = new String[data.length - 1];
                    System.arraycopy(data, 1, userData, 0, data.length - 1);
                    userBookHistory.add(userData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading user's book history from CSV file.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return userBookHistory;
    }

    private void filterBooks(String query) {
        if (query.isEmpty()) {
            showAllBooks();
            return;
        }

        ArrayList<String[]> filteredBooks = new ArrayList<>();
        for (String[] book : allBooks) {
            String title = book[0].toLowerCase();
            String author = book[1].toLowerCase();
            if (title.contains(query) || author.contains(query)) {
                filteredBooks.add(book);
            }
        }

        updateTable(filteredBooks);
    }

    private void showAllBooks() {
        updateTable(allBooks);
    }

    private void updateTable(ArrayList<String[]> books) {
        tableModel.setRowCount(0);
        for (String[] book : books) {
            String title = book[0];
            String author = book[1];
            String rating = calculateAverageRatingForBook(title);
            String reviews = loadReviewsForBook(title);
            tableModel.addRow(new Object[] { title, author, rating, reviews, false });
        }
    }
}
