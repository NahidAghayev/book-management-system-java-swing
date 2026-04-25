package com.bookmanagementsystem;

import javax.swing.SwingUtilities;

import com.bookmanagementsystem.ui.MyGUI;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyGUI gui = new MyGUI();
            gui.setVisible(true);
        });
    }
}
