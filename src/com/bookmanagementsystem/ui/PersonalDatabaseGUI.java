package com.bookmanagementsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.bookmanagementsystem.util.AppPaths;

public class PersonalDatabaseGUI extends JFrame {
    private static final Path PERSONAL_FILE = AppPaths.dataFile("personal.csv");

    private DefaultTableModel tableModel;

    public PersonalDatabaseGUI(ArrayList<String[]> selectedBooks, ArrayList<String[]> userBookHistory, String username) {
        setTitle("Add to library");
        setSize(800, 400);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username: " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setOpaque(true);
        usernameLabel.setBackground(Color.PINK);
        usernameLabel.setForeground(Color.BLACK);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.PINK);
        topPanel.add(usernameLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            GeneralDatabaseGUI showBooks = new GeneralDatabaseGUI(username);
            showBooks.setVisible(true);
        });
        backButton.setBackground(new Color(0, 128, 255));
        backButton.setForeground(Color.WHITE);
        topPanel.add(backButton, BorderLayout.WEST);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {
                "Title", "Author", "Rating", "Reviews", "Status", "Spend Time (minutes)",
                "Start Date", "End Date", "User Rating", "User Review", "Delete"
        };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4;
            }
        };

        for (String[] book : selectedBooks) {
            Object[] rowData = {
                    book[0], book[1], book[2], book[3], "Not Started", "", "", "", "Add rating", "Add review", "Delete"
            };
            tableModel.addRow(rowData);
        }
        for (String[] book : userBookHistory) {
            tableModel.addRow(book);
        }

        JTable addToLibraryTable = new JTable(tableModel);
        addToLibraryTable.setRowHeight(30);

        for (int i = 0; i < 4; i++) {
            addToLibraryTable.getColumnModel().getColumn(i).setCellRenderer(new NonEditableCellRenderer());
        }

        addToLibraryTable.getColumnModel().getColumn(8).setCellEditor(new RatingCellEditor());
        addToLibraryTable.getColumnModel().getColumn(5).setCellEditor(new TimeCellEditor());
        addToLibraryTable.getColumnModel().getColumn(6).setCellEditor(new DateCellEditor(addToLibraryTable));
        addToLibraryTable.getColumnModel().getColumn(7).setCellEditor(new DateCellEditor(addToLibraryTable));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[] { "Not started", "Ongoing", "Completed" });
        addToLibraryTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int row = addToLibraryTable.getSelectedRow();
            if (row != -1) {
                String title = (String) tableModel.getValueAt(row, 0);
                deleteBookByUsernameAndTitle(username, title);
                tableModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        addToLibraryTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        addToLibraryTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(deleteButton));

        JScrollPane scrollPane = new JScrollPane(addToLibraryTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (isDataComplete()) {
                saveData(username);
            } else {
                JOptionPane.showMessageDialog(null, "Please complete all required fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            }
        });
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        getContentPane().add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private static class NonEditableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setEnabled(false);
            return component;
        }
    }

    private static class RatingCellEditor extends DefaultCellEditor {
        public RatingCellEditor() {
            super(new JTextField());
        }

        @Override
        public boolean stopCellEditing() {
            JTextField textField = (JTextField) getComponent();
            String input = textField.getText().trim();
            try {
                double rating = Double.parseDouble(input);
                if (rating < 0 || rating > 5) {
                    JOptionPane.showMessageDialog(null, "Rating must be between 0 and 5.", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return super.stopCellEditing();
        }
    }

    private static class TimeCellEditor extends DefaultCellEditor {
        public TimeCellEditor() {
            super(new JTextField());
        }

        @Override
        public boolean stopCellEditing() {
            JTextField textField = (JTextField) getComponent();
            String input = textField.getText().trim();
            try {
                int time = Integer.parseInt(input);
                if (time < 0) {
                    JOptionPane.showMessageDialog(null, "Time cannot be negative.", "Invalid Time", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return super.stopCellEditing();
        }
    }

    private static class DateCellEditor extends DefaultCellEditor {
        private final JTable table;

        public DateCellEditor(JTable table) {
            super(new JTextField());
            this.table = table;
        }

        @Override
        public boolean stopCellEditing() {
            JTextField textField = (JTextField) getComponent();
            String input = textField.getText().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);

            try {
                Date date = dateFormat.parse(input);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (year < 1900 || year > 2026 || month < 1 || month > 12 || day < 1 || day > 31) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid date in the format YYYY-MM-DD.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if (table.getSelectedColumn() == 7) {
                    String startValue = String.valueOf(table.getValueAt(table.getSelectedRow(), 6)).trim();
                    if (!startValue.isEmpty()) {
                        Date startDate = dateFormat.parse(startValue);
                        if (startDate.after(date)) {
                            JOptionPane.showMessageDialog(null, "Start date cannot be after end date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                    }
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return super.stopCellEditing();
        }
    }

    public boolean isDataComplete() {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (!isDataComplete(row, true)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDataComplete(int row, boolean isNewEntry) {
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            String value = String.valueOf(tableModel.getValueAt(row, col)).trim();
            if (value.isEmpty() || value.equals("Add rating") || value.equals("Add review")) {
                return false;
            }
        }
        return true;
    }

    private void saveData(String username) {
        try {
            ArrayList<String> existingData = new ArrayList<>();
            if (Files.exists(PERSONAL_FILE)) {
                existingData.addAll(Files.readAllLines(PERSONAL_FILE));
            }

            ArrayList<String> linesToAppend = new ArrayList<>();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                StringBuilder rowString = new StringBuilder();
                rowString.append(username).append(",");
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String value = String.valueOf(tableModel.getValueAt(row, col)).trim();
                    rowString.append(value);
                    if (col < tableModel.getColumnCount() - 1) {
                        rowString.append(",");
                    }
                }

                boolean found = false;
                for (int i = 0; i < existingData.size(); i++) {
                    String currentLine = existingData.get(i);
                    if (currentLine.startsWith(username + ",")
                            && currentLine.contains(rowString.toString().split(",")[1])) {
                        existingData.set(i, rowString.toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    linesToAppend.add(rowString.toString());
                }
            }

            existingData.addAll(linesToAppend);
            try (BufferedWriter writer = Files.newBufferedWriter(PERSONAL_FILE)) {
                for (String line : existingData) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            JOptionPane.showMessageDialog(null, "Data saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBookByUsernameAndTitle(String username, String title) {
        try {
            ArrayList<String> existingData = new ArrayList<>();
            if (Files.exists(PERSONAL_FILE)) {
                existingData.addAll(Files.readAllLines(PERSONAL_FILE));
            }

            existingData.removeIf(line -> line.startsWith(username + ",") && line.contains(title));

            try (BufferedWriter writer = Files.newBufferedWriter(PERSONAL_FILE)) {
                for (String line : existingData) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            JOptionPane.showMessageDialog(null, "Book deleted successfully.", "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while deleting the book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JButton button) {
            super(new JTextField());
            this.button = button;
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = value == null ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Button click behavior is handled by the table-level action listener.
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
