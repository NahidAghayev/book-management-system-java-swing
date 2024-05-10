import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/** 
 * GUI class for managing personal library database where users can add, edit, and delete books, along with track reading progress.
 */
public class PersonalDatabaseGUI extends JFrame {

    private DefaultTableModel tableModel;

    /**
     * Constructor initializes the personal database interface.
     * @param selectedBooks Books selected to be added to the personal library.
     * @param userBookHistory Books already present in the user's personal library.
     * @param username Username of the current user.
     */
    public PersonalDatabaseGUI(ArrayList<String[]> selectedBooks, ArrayList<String[]> userBookHistory, String username) {
        setTitle("Add to library");
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Setting up the username label at the top of the window
        JLabel usernameLabel = new JLabel("Username: " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setOpaque(true);
        usernameLabel.setBackground(Color.PINK);
        usernameLabel.setForeground(Color.BLACK);

        // Panel for username label and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.PINK);
        topPanel.add(usernameLabel, BorderLayout.CENTER);

        // Back button to return to the previous screen
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            GeneralDatabaseGUI showBooks = new GeneralDatabaseGUI(username);
            showBooks.setVisible(true);
        });
        backButton.setBackground(new Color(0, 128, 255)); // Dark blue
        backButton.setForeground(Color.WHITE);
        topPanel.add(backButton, BorderLayout.WEST);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Table setup with specified column names
        String[] columnNames = {"Title", "Author", "Rating", "Reviews", "Status", "Spend Time (minutes)", "Start Date", "End Date", "User Rating", "User Review", "Delete"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4; // Make columns from "Status" onwards editable
            }
        };

        // Adding books to the table
        for (String[] book : selectedBooks) {
            Object[] rowData = {book[0], book[1], book[2], book[3], "Not Started", "", "", "", "Add rating", "Add review", "Delete"};
            tableModel.addRow(rowData);
        }
        for (String[] book : userBookHistory) {
            tableModel.addRow(book);
        }

        // Creating the table and setting its properties
        JTable addToLibraryTable = new JTable(tableModel);
        addToLibraryTable.setRowHeight(30);

        // Custom renderers to handle display of non-editable columns
        for (int i = 0; i < 4; i++) {
            addToLibraryTable.getColumnModel().getColumn(i).setCellRenderer(new NonEditableCellRenderer());
        }

        // Custom cell editors for various columns to handle specific data types
        addToLibraryTable.getColumnModel().getColumn(8).setCellEditor(new RatingCellEditor());
        addToLibraryTable.getColumnModel().getColumn(5).setCellEditor(new TimeCellEditor());
        addToLibraryTable.getColumnModel().getColumn(6).setCellEditor(new DateCellEditor(addToLibraryTable));
        addToLibraryTable.getColumnModel().getColumn(7).setCellEditor(new DateCellEditor(addToLibraryTable));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Not started", "Ongoing", "Completed"});
        addToLibraryTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Adding a button to delete rows
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

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(addToLibraryTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Button to save the changes made to the table data
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (isDataComplete()) {
                saveData(username);
            } else {
                JOptionPane.showMessageDialog(null, "Please complete all required fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            }
        });
        saveButton.setBackground(new Color(34, 139, 34)); // Dark green
        saveButton.setForeground(Color.WHITE);
        getContentPane().add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Inner class for custom rendering of non-editable cells
    private static class NonEditableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setEnabled(false); // Disabling editing
            return component;
        }
    }

    // Custom cell editor for "User Rating" to ensure the input is a number between 0 and 5
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

    // Custom cell editor for "Spend Time (minutes)" to ensure the input is a non-negative integer
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

    // Custom cell editor for "Start Date" and "End Date" to ensure the input is a valid date within a specified range
    private static class DateCellEditor extends DefaultCellEditor {
        private JTable table;

        public DateCellEditor(JTable table) {
            super(new JTextField());
            this.table = table;
        }

        @Override
        public boolean stopCellEditing() {
            JTextField textField = (JTextField) getComponent();
            String input = textField.getText().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // Ensure strict adherence to format

            try {
                Date date = dateFormat.parse(input);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Validate year, month, and day ranges
                if (year < 1900 || year > 2024 || month < 1 || month > 12 || day < 1 || day > 31) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid date in the format YYYY-MM-DD.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                // For end date, ensure it does not precede the start date
                if (table.getSelectedColumn() == 7) {
                    Date startDate = dateFormat.parse((String) table.getValueAt(table.getSelectedRow(), 6));
                    if (startDate.after(date)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be after end date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return super.stopCellEditing();
        }
    }

    // Check if data in the table is complete and valid
    public boolean isDataComplete() {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (!isDataComplete(row, true)) {
                return false;
            }
        }
        return true;
    }

    // Helper method to check if data in a specific row is complete
    public boolean isDataComplete(int row, boolean isNewEntry) {
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            String value = String.valueOf(tableModel.getValueAt(row, col)).trim();
            if (value.isEmpty() || value.equals("Add rating") || value.equals("Add review")) {
                return false; // Data is incomplete or placeholder
            }
        }
        return true;
    }

    // Save data from the table to a file, appending new entries and updating existing ones
    private void saveData(String username) {
        try {
            String fileName = "personal.csv";
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true); // Open in append mode
            BufferedWriter bw = new BufferedWriter(fw);

            ArrayList<String> existingData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                existingData.add(scanner.nextLine());
            }
            scanner.close();

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
                    if (existingData.get(i).startsWith(username) && existingData.get(i).contains(rowString.toString().split(",")[1])) {
                        existingData.set(i, rowString.toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    bw.write(rowString.toString());
                    bw.newLine();
                }
            }
            FileWriter fileWriter = new FileWriter(fileName);
            for (String line : existingData) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            fileWriter.close();
            bw.close();
            JOptionPane.showMessageDialog(null, "Data saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a book entry from the file based on username and title
    private void deleteBookByUsernameAndTitle(String username, String title) {
        try {
            String fileName = "personal.csv";
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);

            ArrayList<String> existingData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                existingData.add(scanner.nextLine());
            }
            scanner.close();

            for (int i = 0; i < existingData.size(); i++) {
                String line = existingData.get(i);
                if (line.startsWith(username) && line.contains(title)) {
                    existingData.remove(i);
                    i--;
                }
            }
            FileWriter fileWriter = new FileWriter(fileName);
            for (String line : existingData) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            fileWriter.close();
            bw.close();
            JOptionPane.showMessageDialog(null, "Book deleted successfully.", "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while deleting the book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Renderer for button in a table cell
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Editor for button in a table cell, providing interactive functionality
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

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Logic for button interaction
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
