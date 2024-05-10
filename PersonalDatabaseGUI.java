import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;

public class PersonalDatabaseGUI extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private JButton backButton;
    private JButton saveButton;
    private String username;
    private boolean isAdmin;
    private List<String[]> selectedBooks = new ArrayList<>();
    private Map<String, String[]> userReviews = new HashMap<>(); // Map to store user ratings and reviews
    private static final String CSV_FILE_PATH = "user_reviews.csv"; // Path to the CSV file

    private ResourceBundle messages;

    public static PersonalDatabaseGUI instance;

    public PersonalDatabaseGUI(ResourceBundle messages) {
        this.messages = messages;
        initializeUI();
    }
    private void initializeUI() {
        setTitle(messages.getString("app.title"));
    }
    public PersonalDatabaseGUI(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;

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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::performSearch);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        buttonPanel.add(backButton, BorderLayout.WEST);
        buttonPanel.add(searchPanel, BorderLayout.CENTER);

        saveButton = new JButton("Save");
        buttonPanel.add(saveButton, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        backButton.addActionListener(e -> {
            dispose();
            new TransitionPage(isAdmin);
        });

        saveButton.addActionListener(e -> saveUserInfo());

        setVisible(true);
        initUI();
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

    public void addSelectedBooks(List<String[]> books) {
        for (String[] book : books) {
            model.addRow(new Object[]{
                book[0], // Title
                book[1], // Author
                book[2], // Rating
                book[3], // Reviews
                "Not started", // Status
                "", // Time Spent
                "", // Start Date
                "", // End Date
                "Add rating", // User Rating
                "Add review" // User Review
            });
        }
        table.revalidate();
        table.repaint();
    }

    private void saveUserInfo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            // Write headers to the CSV file
            writer.write("Title,User Rating,User Review");
            writer.newLine();
            
            // Iterate through each row of the table
            for (int i = 0; i < model.getRowCount(); i++) {
                String title = (String) model.getValueAt(i, 0);
                String userRating = (String) model.getValueAt(i, 8);
                String userReview = (String) model.getValueAt(i, 9);

                // Write user rating and review to the CSV file
                writer.write(title + "," + userRating + "," + userReview);
                writer.newLine();
                
                // Store user rating and review for the book title in the map
                userReviews.put(title, new String[]{userRating, userReview});
            }

            // Display a message indicating successful save
            JOptionPane.showMessageDialog(this, "User information saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save user information!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void initUI() {
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make review columns editable directly from the table
                return column == 8 || column == 9;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 8 || columnIndex == 9) {
                    return JButton.class;
                }
                return String.class;
            }
        };

        // Define columns
        String[] columns = new String[]{"Title", "Author", "Rating", "Reviews", "Status", "Time Spent", "Start Date", "End Date", "User Rating", "User Review"};
        for (String column : columns) {
            model.addColumn(column);
        }

        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(950, 300));
        table.setFillsViewportHeight(true);

        // Adding custom renderers for clickable review buttons
        table.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
    }

    // Custom button renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Click to add" : value.toString());
            return this;
        }
    }

    // Custom button editor for review column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
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
            label = (value == null) ? "Click to add" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                JOptionPane.showMessageDialog(button, "Open editable review window here");
                // Logic to open an editable window for review
            }
            isPushed = false;
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonalDatabaseGUI("defaultUser", false));
    }
}
