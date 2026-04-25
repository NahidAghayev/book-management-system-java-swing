package com.bookmanagementsystem.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TransitionPage extends JFrame {
    private final boolean isAdmin;
    private final String username;

    public TransitionPage(String username, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.username = username;
        setTitle("Book Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.PINK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Welcome to the Book Management System!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, gbc);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new MyGUI().setVisible(true);
        });

        gbc.gridy++;
        mainPanel.add(backButton, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generalDatabaseButton = new JButton("General Database");
        generalDatabaseButton.setBackground(new Color(0, 128, 255));
        generalDatabaseButton.setForeground(Color.WHITE);
        generalDatabaseButton.addActionListener(e -> {
            dispose();
            new GeneralDatabaseGUI(username).setVisible(true);
        });
        buttonPanel.add(generalDatabaseButton);

        if (!isAdmin) {
            JButton personalDatabaseButton = new JButton("Personal Database");
            personalDatabaseButton.setBackground(new Color(34, 139, 34));
            personalDatabaseButton.setForeground(Color.WHITE);
            personalDatabaseButton.addActionListener(e -> {
                dispose();
                new PersonalDatabaseGUI(new java.util.ArrayList<>(), new java.util.ArrayList<>(), username).setVisible(true);
            });
            buttonPanel.add(personalDatabaseButton);
        }

        gbc.gridy++;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new TransitionPage("guest", false);
    }
}
