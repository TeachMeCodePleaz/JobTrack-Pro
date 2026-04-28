package com.jobtrack.ui;

import com.jobtrack.dao.ApplicationDao;
import com.jobtrack.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainDashboard extends JFrame {
    private User currentUser;
    private DefaultTableModel tableModel;

    public MainDashboard(User user) {
        this.currentUser = user;
        setTitle("JobTrack Pro - " + user.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        refreshTableData(); // Load data on startup
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table
        String[] columns = {"ID", "Company", "Position", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        // Controls
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Application");
        addBtn.addActionListener(e -> {
            new AddApplicationDialog(this, currentUser.getId(), this::refreshTableData).setVisible(true);
        });
        btnPanel.add(addBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void refreshTableData() {
        ApplicationDao.getApplicationsByUserId(currentUser.getId()).thenAccept(data -> {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (String[] row : data) tableModel.addRow(row);
            });
        });
    }
}