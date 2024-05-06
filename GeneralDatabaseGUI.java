import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralDatabaseGUI extends JFrame {
    private JPanel mainPanel;
    private JTable bookTable;
    private JTextField searchField;
    private JButton searchButton;
    private JButton backButton; // Added Back button
    private DefaultTableModel tableModel;
    private List<String[]> originalData;
    private boolean isAdmin; // Field to store admin status

    public GeneralDatabaseGUI(boolean isAdmin) {
        this.isAdmin = isAdmin; // Initialize isAdmin field

        setTitle("General Database");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create a panel to hold Back button and search panel
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Added Back button
        backButton = new JButton("Back");
        buttonPanel.add(backButton, BorderLayout.WEST); // Add the Back button to the WEST position

        // Create search functionality
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        buttonPanel.add(searchPanel, BorderLayout.CENTER); // Add search panel to the CENTER position
        mainPanel.add(buttonPanel, BorderLayout.NORTH); // Add button panel to the NORTH position

        // Create a table to display book data
        bookTable = new JTable();
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with data from CSV file
        populateTableFromCSV("brodsky.csv");

        // Add action listener for search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().toLowerCase();
                if (!searchTerm.isEmpty()) {
                    List<String[]> filteredData = new ArrayList<>();
                    for (String[] bookData : originalData) {
                        for (String data : bookData) {
                            if (data.toLowerCase().contains(searchTerm)) {
                                filteredData.add(bookData);
                                break;
                            }
                        }
                    }
                    updateTable(filteredData);
                } else {
                    updateTable(originalData);
                }
            }
        });

        // Added action listener for Back button to dispose the current frame
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TransitionPage(isAdmin); // Pass isAdmin to TransitionPage constructor
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private void populateTableFromCSV(String filePath) {
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Reviews");
        tableModel.addColumn("Add Book"); // Add "Add Book" column

        originalData = readBooksFromCSV(filePath);
        for (String[] bookData : originalData) {
            String title = bookData[0];
            String author = bookData[1];
            String rating = "No rating";
            String reviews = "No review";
            tableModel.addRow(new String[]{title, author, rating, reviews, "Add"}); // Add "Add" button for each book
        }

        bookTable.setModel(tableModel);
        bookTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private List<String[]> readBooksFromCSV(String filePath) {
        List<String[]> booksData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                String[] titles = data[0].split(",\\s*"); // Split titles if comma-separated
                for (String title : titles) {
                    title = title.trim(); // Trim any leading or trailing spaces
                    title = (title.isEmpty()) ? "Unknown" : title; // Use "Unknown" if title is empty
                    String author = (data.length > 1 && !data[1].trim().isEmpty()) ? data[1].trim() : "Unknown"; // Check if author is present
                    booksData.add(new String[]{title, author});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booksData;
    }

    private String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values.toArray(new String[0]);
    }

    private void updateTable(List<String[]> newData) {
        tableModel.setRowCount(0); // Clear existing rows
        for (String[] bookData : newData) {
            tableModel.addRow(bookData);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    // Perform the action you want when the button is clicked
                    // For example, add the book to the cart or open a dialog for more details
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneralDatabaseGUI(false));
    }
}
