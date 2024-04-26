import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private DefaultTableModel tableModel;
    private List<String[]> originalData;

    public GeneralDatabaseGUI() {
        setTitle("General Database");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create a table to display book data
        bookTable = new JTable();
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create search functionality
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

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
        };

        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Reviews");

        originalData = readBooksFromCSV(filePath);
        for (String[] bookData : originalData) {
            String title = bookData[0];
            String author = bookData[1];
            String rating = "No rating";
            String reviews = "No review";
            tableModel.addRow(new String[]{title, author, rating, reviews});
        }

        bookTable.setModel(tableModel);
    }

    private List<String[]> readBooksFromCSV(String filePath) {
        List<String[]> booksData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                String title = (data.length > 0 && !data[0].trim().isEmpty()) ? data[0].trim() : "Unknown";
                String author = (data.length > 1 && !data[1].trim().isEmpty()) ? data[1].trim() : "Unknown"; // Check if author is present
                booksData.add(new String[]{title, author});
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneralDatabaseGUI());
    }
}
