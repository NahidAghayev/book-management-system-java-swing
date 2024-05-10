
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
    private JButton backButton;
    private JButton addButton;
    private DefaultTableModel tableModel;
    private List<String[]> originalData;
    private boolean isAdmin;

    public GeneralDatabaseGUI(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("General Database");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        backButton = new JButton("Back");
        buttonPanel.add(backButton);

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        buttonPanel.add(searchPanel);

        addButton = new JButton("Add");
        buttonPanel.add(addButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        bookTable = new JTable();
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        populateTableFromCSV("brodsky.csv");

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

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = bookTable.getSelectedRows();
                List<String[]> selectedBooks = new ArrayList<>();
                for (int viewRow : selectedRows) {
                    int modelRow = bookTable.convertRowIndexToModel(viewRow);
                    String title = (String) tableModel.getValueAt(modelRow, 0);
                    String author = (String) tableModel.getValueAt(modelRow, 1);
                    String rating = (String) tableModel.getValueAt(modelRow, 2);
                    String reviews = (String) tableModel.getValueAt(modelRow, 3);
                    selectedBooks.add(new String[]{title, author, rating, reviews});
                }

                // New logic for adding selected books
                addToLibrary(selectedBooks);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TransitionPage(isAdmin);
            }
        });

        add(mainPanel);
        setVisible(true);

        
    }

    private void populateTableFromCSV(String filePath) {
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) {
                    return Boolean.class;
                } else if (columnIndex == 2 || columnIndex == 3) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == getColumnCount() - 1;
            }
        };

        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Reviews");
        tableModel.addColumn("Selected Books");

        originalData = readBooksFromCSV(filePath);
        for (String[] bookData : originalData) {
            String title = bookData[0];
            String author = bookData[1];
            String rating = "No rating";
            String reviews = "No review";
            tableModel.addRow(new Object[]{title, author, rating, reviews, false});
        }

        bookTable.setModel(tableModel);
    }

    private List<String[]> readBooksFromCSV(String filePath) {
        List<String[]> booksData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                String[] titles = data[0].split(",\\s*");
                for (String title : titles) {
                    title = title.trim();
                    title = (title.isEmpty()) ? "Unknown" : title;
                    String author = (data.length > 1 && !data[1].trim().isEmpty()) ? data[1].trim() : "Unknown";
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
        tableModel.setRowCount(0);
        for (String[] bookData : newData) {
            tableModel.addRow(new Object[]{bookData[0], bookData[1], "No rating", "No review", false});
        }
    }

    private void addToLibrary(List<String[]> selectedBooks) {
        if (PersonalDatabaseGUI.instance != null) {
            PersonalDatabaseGUI.instance.addSelectedBooks(selectedBooks);
            PersonalDatabaseGUI.instance.setVisible(true);
        } else {
            PersonalDatabaseGUI.instance = new PersonalDatabaseGUI("username", false);
            PersonalDatabaseGUI.instance.addSelectedBooks(selectedBooks);
            PersonalDatabaseGUI.instance.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneralDatabaseGUI(false));
    }
}



