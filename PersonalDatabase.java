import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class PersonalDatabase extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTitle, txtAuthor,txtRating,txtReviews, txtUserRating, txtUserReview;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete;

    public PersonalDatabase() {
        super("Personal Book Management System");

        createTable();
        createForm();

        btnAdd = new JButton("Add Book");
        btnUpdate = new JButton("Update Selected Book");
        btnDelete = new JButton("Delete Selected Book");

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(btnAdd);
        inputPanel.add(btnUpdate);
        inputPanel.add(btnDelete);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 400);
        this.setVisible(true);
    }

    private void createTable() {
        String[] columnNames = {"Title", "Author","Rating", "Reviews", "Status", "Time Spent", "Start Date", "End Date", "User Rating", "User Review"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
    }

    private void createForm() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2));

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

        formPanel.add(new JLabel("User Rating:"));
        txtUserRating = new JTextField(20);
        formPanel.add(txtUserRating);

        formPanel.add(new JLabel("User Review:"));
        txtUserReview = new JTextField(20);
        formPanel.add(txtUserReview);

        this.add(formPanel, BorderLayout.NORTH);
    }

    private void addBook() {
        model.addRow(new Object[]{
            txtTitle.getText(),
            txtAuthor.getText(),
            cbStatus.getSelectedItem().toString(),
            "", // Time spent
            "", // Start date
            "", // End date
            txtUserRating.getText(),
            txtUserReview.getText()
        });
        clearForm();
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.setValueAt(txtTitle.getText(), selectedRow, 0);
            model.setValueAt(txtAuthor.getText(), selectedRow, 1);
            model.setValueAt(txtRating.getText(), selectedRow, 2);
            model.setValueAt(txtReviews.getText(), selectedRow, 3);
            model.setValueAt(cbStatus.getSelectedItem().toString(), selectedRow, 4);
            model.setValueAt(txtUserRating.getText(), selectedRow, 6);
            model.setValueAt(txtUserReview.getText(), selectedRow, 7);
            clearForm();
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
        cbStatus.setSelectedIndex(0);
        txtUserRating.setText("");
        txtUserReview.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonalDatabase().setVisible(true));
    }
    }


