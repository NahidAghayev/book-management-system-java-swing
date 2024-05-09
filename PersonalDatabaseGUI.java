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

public class PersonalDatabaseGUI extends JFrame {

    private DefaultTableModel tableModel;

    public PersonalDatabaseGUI(ArrayList<String[]> selectedBooks, ArrayList<String[]> userBookHistory, String username) {
        setTitle("Add to library");
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Set up username label at the top
        JLabel usernameLabel = new JLabel("Username: " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setOpaque(true);
        usernameLabel.setBackground(Color.PINK);
        usernameLabel.setForeground(Color.BLACK);

        // Create a panel for the username label and the back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.PINK);
        topPanel.add(usernameLabel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose(); // Close the current frame
            GeneralDatabaseGUI showBooks = new GeneralDatabaseGUI(username); // Create new instance of ShowBooks
            showBooks.setVisible(true); // Show the ShowBooks frame
        });
        backButton.setBackground(new Color(0, 128, 255)); // Dark blue
        backButton.setForeground(Color.WHITE);

        // Add the back button to the panel
        topPanel.add(backButton, BorderLayout.WEST);

        // Add the panel to the NORTH of the content pane
        getContentPane().add(topPanel, BorderLayout.NORTH);


        // Set up table and its model
        String[] columnNames = {"Title", "Author", "Rating", "Reviews", "Status", "Spend Time (minutes)", "Start Date", "End Date", "User Rating", "User Review", "Delete"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4; // Editable columns start from index 4 onwards
            }
        };

        // Add selected books with default values
        for (String[] book : selectedBooks) {
            Object[] rowData = {book[0], book[1], book[2], book[3], "Not Started", "", "", "", "Add rating", "Add review", "Delete"};
            tableModel.addRow(rowData);
        }

        // Add user's book history
        for (String[] book : userBookHistory) {
            tableModel.addRow(book);
        }


        // Create the table
        JTable addToLibraryTable = new JTable(tableModel);
        addToLibraryTable.setRowHeight(30);

        // Set custom cell renderer for non-editable columns
        for (int i = 0; i < 4; i++) {
            addToLibraryTable.getColumnModel().getColumn(i).setCellRenderer(new NonEditableCellRenderer());
        }

        // Set custom cell editor for "User Rating" column
        addToLibraryTable.getColumnModel().getColumn(8).setCellEditor(new RatingCellEditor());

        // Set custom cell editor for "Spend Time (minutes)" column
        addToLibraryTable.getColumnModel().getColumn(5).setCellEditor(new TimeCellEditor());

        // Set custom cell editor for "Start Date" and "End Date" columns
        addToLibraryTable.getColumnModel().getColumn(6).setCellEditor(new DateCellEditor(addToLibraryTable));
        addToLibraryTable.getColumnModel().getColumn(7).setCellEditor(new DateCellEditor(addToLibraryTable));

        // Set custom cell editor for "Status" column
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Not started", "Ongoing", "Completed"});
        addToLibraryTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Add delete button to each row
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int row = addToLibraryTable.getSelectedRow();
            if (row != -1) { // Check if a row is selected
                String title = (String) tableModel.getValueAt(row, 0);
                deleteBookByUsernameAndTitle(username, title);
                tableModel.removeRow(row); // Remove the row from the table
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        addToLibraryTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        addToLibraryTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(deleteButton));

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(addToLibraryTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        FilterAndSortFunction.SortSelected(addToLibraryTable);

        // Add Save button
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

    // Custom cell renderer to make specific columns non-editable
    private static class NonEditableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setEnabled(false); // Disable editing
            return component;
        }
    }

    // Custom cell editor for "User Rating" column
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
                    return false; // Don't stop editing
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return false; // Don't stop editing
            }
            return super.stopCellEditing();
        }
    }

    // Custom cell editor for "Spend Time (minutes)" column
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
                    return false; // Don't stop editing
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return false; // Don't stop editing
            }
            return super.stopCellEditing();
        }
    }

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
            dateFormat.setLenient(false); // Disable lenient parsing

            try {
                Date date = dateFormat.parse(input);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Check year range
                if (year < 1900 || year > 2030) {
                    JOptionPane.showMessageDialog(null, "Year must be between 1900 and 2030.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return false; // Don't stop editing
                }

                // Check month range
                if (month < 1 || month > 12) {
                    JOptionPane.showMessageDialog(null, "Month must be between 1 and 12.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return false; // Don't stop editing
                }
                // Check day range
                if (day < 1 || day > 31) {
                    JOptionPane.showMessageDialog(null, "Day must be between 1 and 31.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return false; // Don't stop editing
                }

                int column = table.getSelectedColumn();
                if (column == 7) { // Check if the end date column is being edited
                    Date startDate = dateFormat.parse(table.getValueAt(table.getSelectedRow(), 6).toString());
                    // Check if start date is after end date
                    if (startDate.after(date)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be after end date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return false; // Don't stop editing
                    }
                }

            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return false; // Don't stop editing
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
            // Check if the value is empty and it's a required field
            if (value.isEmpty() && !((col == 8 && value.equals("Add rating")) || (col == 9 && value.equals("Add review")) || (isNewEntry && (col < 4)))) {
                return false; // Incomplete data found
            } else if (value.equals("Add rating") || value.equals("Add review")) {
                return false; // Placeholder value found
            }
        }
        return true;
    }

    private void saveData(String username) {
        try {
            // File to write
            String fileName = "personal.csv";
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true); // Append mode
            BufferedWriter bw = new BufferedWriter(fw);

            // Read existing data from the file
            ArrayList<String> existingData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                existingData.add(scanner.nextLine());
            }
            scanner.close();

            // Append data for each row in the table model
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                StringBuilder rowString = new StringBuilder();
                // Append username to each row
                rowString.append(username).append(",");
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String value = String.valueOf(tableModel.getValueAt(row, col)).trim();
                    // Append the value with comma separator
                    rowString.append(value);
                    if (col < tableModel.getColumnCount() - 1) {
                        rowString.append(",");
                    }
                }
                boolean found = false;
                // Check if the rowString already exists in the file
                for (int i = 0; i < existingData.size(); i++) {
                    if (existingData.get(i).startsWith(username) && existingData.get(i).contains(rowString.toString().split(",")[1])) {
                        existingData.set(i, rowString.toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // Write the row to the file
                    bw.write(rowString.toString());
                    bw.newLine(); // Add new line for the next row
                }
            }

            // Write updated data back to the file
            FileWriter fileWriter = new FileWriter(fileName);
            for (String line : existingData) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            fileWriter.close();

            // Close the BufferedWriter
            bw.close();

            // Show success message
            JOptionPane.showMessageDialog(null, "Data saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBookByUsernameAndTitle(String username, String title) {
        try {
            // File to write
            String fileName = "personal.csv";
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true); // Append mode
            BufferedWriter bw = new BufferedWriter(fw);

            // Read existing data from the file
            ArrayList<String> existingData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                existingData.add(scanner.nextLine());
            }
            scanner.close();

            // Remove the book entry for the given username and title
            for (int i = 0; i < existingData.size(); i++) {
                String line = existingData.get(i);
                if (line.startsWith(username) && line.contains(title)) {
                    existingData.remove(i);
                    i--; // Adjust the index as we removed an element
                }
            }

            // Write updated data back to the file
            FileWriter fileWriter = new FileWriter(fileName);
            for (String line : existingData) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            fileWriter.close();

            // Close the BufferedWriter
            bw.close();

            // Show success message
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
                // Perform delete operation
                //...
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
