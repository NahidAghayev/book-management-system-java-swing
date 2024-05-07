import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalDatabase extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTitle, txtAuthor, txtRating, txtReviews, txtUserRating, txtUserReview, txtTimeSpent, txtStartDate, txtEndDate;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PersonalDatabase() {
        super("Personal Book Management System");

        createTable();
        createForm();

        btnAdd = new JButton("Add Book");
        btnUpdate = new JButton("Update Selected Book");
        btnDelete = new JButton("Delete Selected Book");

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());

        JPanel inputPanel = new JPanel();
        inputPanel.add(btnAdd);
        inputPanel.add(btnUpdate);
        inputPanel.add(btnDelete);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 500);
        this.setVisible(true);
    }

    private void createTable() {
        String[] columnNames = {"Title", "Author", "Rating", "Reviews", "Status", "Time Spent", "Start Date", "End Date", "User Rating", "User Review"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // To prevent user from editing the cells directly in the table
            }
        };
        table = new JTable(model);
    }

    private void createForm() {
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField(20);
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField(20);
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Rating:"));
        txtRating = new JTextField(20);
        formPanel.add(txtRating);

        formPanel.add(new JLabel("Reviews:"));
        txtReviews = new JTextField(20);
        formPanel.add(txtReviews);

        formPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Not Started", "Ongoing", "Completed"});
        formPanel.add(cbStatus);

        formPanel.add(new JLabel("Time Spent (minutes):"));
        txtTimeSpent = new JTextField(20);
        formPanel.add(txtTimeSpent);

        formPanel.add(new JLabel("Start Date (dd/mm/yyyy):"));
        txtStartDate = new JTextField(20);
        formPanel.add(txtStartDate);

        formPanel.add(new JLabel("End Date (dd/mm/yyyy):"));
        txtEndDate = new JTextField(20);
        formPanel.add(txtEndDate);

        formPanel.add(new JLabel("User Rating:"));
        txtUserRating = new JTextField(20);
        formPanel.add(txtUserRating);

        formPanel.add(new JLabel("User Review:"));
        txtUserReview = new JTextField(20);
        formPanel.add(txtUserReview);

        this.add(formPanel, BorderLayout.NORTH);
    }

    private void addBook() {
        try {
            model.addRow(new Object[]{
                txtTitle.getText(),
                txtAuthor.getText(),
                txtRating.getText(),
                txtReviews.getText(),
                cbStatus.getSelectedItem().toString(),
                txtTimeSpent.getText(),
                dateFormat.format(dateFormat.parse(txtStartDate.getText())),
                dateFormat.format(dateFormat.parse(txtEndDate.getText())),
                txtUserRating.getText(),
                txtUserReview.getText()
            });
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error in date format or other fields: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                model.setValueAt(txtTitle.getText(), selectedRow, 0);
                model.setValueAt(txtAuthor.getText(), selectedRow, 1);
                model.setValueAt(txtRating.getText(), selectedRow, 2);
                model.setValueAt(txtReviews.getText(), selectedRow, 3);
                model.setValueAt(cbStatus.getSelectedItem().toString(), selectedRow, 4);
                model.setValueAt(txtTimeSpent.getText(), selectedRow, 5);
                model.setValueAt(dateFormat.format(dateFormat.parse(txtStartDate.getText())), selectedRow, 6);
                model.setValueAt(dateFormat.format(dateFormat.parse(txtEndDate.getText())), selectedRow, 7);
                model.setValueAt(txtUserRating.getText(), selectedRow, 8);
                model.setValueAt(txtUserReview.getText(), selectedRow, 9);
                clearForm();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error in date format or other fields: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
        }
    }

    private void clearForm() {
        txtTitle.setText("");
        txtAuthor.setText("");
        txtRating.setText("");
        txtReviews.setText("");
        cbStatus.setSelectedIndex(0);
        txtTimeSpent.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtUserRating.setText("");
        txtUserReview.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonalDatabase().setVisible(true));
    }
}
