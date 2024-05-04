import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PersonalDatabaseGUI extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public PersonalDatabaseGUI() {
        setTitle("Personal Database");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        model.addColumn("Title");
        model.addColumn("Author");
        model.addColumn("Rating");
        model.addColumn("Reviews");
        model.addColumn("Status");
        model.addColumn("Time Spent");
        model.addColumn("Start Date");
        model.addColumn("End Date");
        model.addColumn("User Rating");
        model.addColumn("User Review");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::performSearch);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Sorter for the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Register a listener for search
        searchField.addActionListener(this::performSearch);
    }

    private void performSearch(ActionEvent e) {
        String text = searchField.getText();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void addBook(String title, String author) {
        // Add the book to the personal database
        // For demonstration purposes, let's just add a dummy entry
        model.addRow(new Object[]{title, author, "", "", "Not started", "", "", "", "Add rating", "Add review"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonalDatabaseGUI().setVisible(true));
    }
}
