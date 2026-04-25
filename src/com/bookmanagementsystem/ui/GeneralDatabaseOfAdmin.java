package com.bookmanagementsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.DefaultListModel;

import com.bookmanagementsystem.util.AppPaths;

public class GeneralDatabaseOfAdmin extends JFrame {
    private static final Path USERS_FILE = AppPaths.dataFile("users.csv");
    private static final Path BOOKS_FILE = AppPaths.dataFile("copy.csv");
    private static final Path GENERAL_FILE = AppPaths.dataFile("general.csv");

    private DefaultTableModel tableModel;
    private DefaultTableModel titlesTableModel;
    private DefaultTableModel reviewsTableModel;
    private ArrayList<String[]> allBooks;
    private ArrayList<String[]> allUsers;
    private ArrayList<String[]> allReviews;

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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.PINK);

        JLabel headerLabel = new JLabel("Book Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 31));
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
        searchPanel.setBackground(Color.PINK);
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
    }

    private void createUsersPanel(JPanel usersPanel) {
        JList<String> userList = new JList<>();
        loadUsers(userList);
        JScrollPane scrollPane = new JScrollPane(userList);
        usersPanel.add(scrollPane, BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected User");
        deleteButton.addActionListener(e -> deleteUser(userList.getSelectedValue(), userList));
        usersPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    private void loadUsers(JList<String> userList) {
        allUsers = new ArrayList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        try (BufferedReader reader = Files.newBufferedReader(USERS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                allUsers.add(data);
                model.addElement(data[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        userList.setModel(model);
    }

    private void deleteUser(String selectedUser, JList<String> userList) {
        if (selectedUser == null) {
            return;
        }

        allUsers.removeIf(user -> user.length > 0 && user[0].equals(selectedUser));
        saveUsersToCSV();
        loadUsers(userList);
    }

    private void saveUsersToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE)) {
            for (String[] user : allUsers) {
                writer.write(String.join(",", user));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTitlesPanel(JPanel titlesPanel) {
        titlesTableModel = new DefaultTableModel(new Object[] { "Title", "Author" }, 0);
        JTable table = new JTable(titlesTableModel);
        table.setDefaultRenderer(Object.class, new CustomRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        titlesPanel.add(scrollPane, BorderLayout.CENTER);

        loadTitles();
        JButton deleteButton = new JButton("Delete Selected Books");
        deleteButton.addActionListener(e -> deleteSelectedTitles(table.getSelectedRows()));
        titlesPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    private void deleteSelectedTitles(int[] selectedRows) {
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int selectedRow = selectedRows[i];
            titlesTableModel.removeRow(selectedRow);
            allBooks.remove(selectedRow);
        }
        saveTitlesToCSV();
    }

    private void saveTitlesToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(BOOKS_FILE)) {
            for (String[] book : allBooks) {
                writer.write(String.join(",", book));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving titles to CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTitles() {
        allBooks = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(BOOKS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String title = data[0].trim().isEmpty() ? "Unknown" : data[0].trim();
                    String author = data[1].trim();
                    allBooks.add(new String[] { title, author });
                    titlesTableModel.addRow(new Object[] { title, author });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUserReviewsPanel(JPanel reviewsPanel) {
        reviewsTableModel = new DefaultTableModel(new Object[] { "Title", "Author", "Rating", "Reviews" }, 0);
        JTable table = new JTable(reviewsTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        reviewsPanel.add(scrollPane, BorderLayout.CENTER);

        loadReviews();
        JButton deleteButton = new JButton("Delete Selected Reviews");
        deleteButton.addActionListener(e -> deleteSelectedReviews(table.getSelectedRows()));
        reviewsPanel.add(deleteButton, BorderLayout.SOUTH);
    }

    private void loadReviews() {
        allReviews = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(GENERAL_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String title = data[0].trim();
                    String author = data[1].trim();
                    String rating = data[2].trim();
                    String reviews = data[3].trim();
                    allReviews.add(new String[] { title, author, rating, reviews });
                    reviewsTableModel.addRow(new Object[] { title, author, rating, reviews });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedReviews(int[] selectedRows) {
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int selectedRow = selectedRows[i];
            reviewsTableModel.removeRow(selectedRow);
            allReviews.remove(selectedRow);
        }
        saveReviewsToCSV();
    }

    private void saveReviewsToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(GENERAL_FILE)) {
            for (String[] review : allReviews) {
                writer.write(String.join(",", review));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving reviews to CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GeneralDatabaseOfAdmin::new);
    }

    private void showAllBooks() {
        updateTable(allBooks);
    }

    private void updateTable(ArrayList<String[]> books) {
        tableModel.setRowCount(0);
        for (String[] book : books) {
            tableModel.addRow(new Object[] { book[0], book[1], "No rating", "No reviews", false });
        }
    }

    private class CustomRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setBackground(Color.PINK);
            return component;
        }
    }
}
