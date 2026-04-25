package com.bookmanagementsystem.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FilterAndSortFunction {
    private static final int ASCENDING = 0;
    private static final int DESCENDING = 1;
    private static final int ORIGINAL = 2;

    private static final Map<Integer, Integer> sortStates = new HashMap<>();
    private static ArrayList<Object[]> dataInitial;

    public static void sortSelected(JTable table) {
        initializeTable(table);
        table.getTableHeader().addMouseListener(new SortMouseListener(table));
    }

    private static void initializeTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        dataInitial = new ArrayList<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object[] currentRow = new Object[model.getColumnCount()];
            for (int column = 0; column < model.getColumnCount(); column++) {
                currentRow[column] = model.getValueAt(row, column);
            }
            dataInitial.add(currentRow);
        }
    }

    static class SortMouseListener extends MouseAdapter {
        private final JTable table;

        SortMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int columnIndex = table.columnAtPoint(e.getPoint());
            if (columnIndex != -1) {
                int currentState = sortStates.getOrDefault(columnIndex, ORIGINAL);
                currentState = (currentState + 1) % 3;
                sortStates.put(columnIndex, currentState);

                DefaultTableModel currentModel = (DefaultTableModel) table.getModel();
                ArrayList<Object[]> data = new ArrayList<>(dataInitial);
                data.sort(new MultiColumnComparator(sortStates));

                currentModel.setRowCount(0);
                for (Object[] row : data) {
                    currentModel.addRow(row);
                }
            }
        }
    }

    static class MultiColumnComparator implements Comparator<Object[]> {
        private final Map<Integer, Integer> sortStates;

        MultiColumnComparator(Map<Integer, Integer> sortStates) {
            this.sortStates = sortStates;
        }

        @Override
        public int compare(Object[] left, Object[] right) {
            for (Map.Entry<Integer, Integer> entry : sortStates.entrySet()) {
                int columnIndex = entry.getKey();
                int sortOrder = entry.getValue();

                @SuppressWarnings("unchecked")
                Comparable<Object> value1 = (Comparable<Object>) left[columnIndex];
                @SuppressWarnings("unchecked")
                Comparable<Object> value2 = (Comparable<Object>) right[columnIndex];

                int result = value1.compareTo(value2);
                if (result != 0) {
                    return sortOrder == ASCENDING ? result : sortOrder == DESCENDING ? -result : 0;
                }
            }
            return 0;
        }
    }
}
