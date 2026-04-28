package com.jobtrack.ui;

import com.jobtrack.dao.ApplicationDao;
import javax.swing.*;
import java.awt.*;

public class AddApplicationDialog extends JDialog {
    private JTextField companyField = new JTextField();
    private JTextField posField = new JTextField();
    private JComboBox<String> statusBox = new JComboBox<>(new String[]{"Applied", "OA Pending", "Interview", "Offer", "Rejected"});
    private JTextField notesField = new JTextField();
    private JButton saveButton = new JButton("Save");

    public AddApplicationDialog(JFrame parent, int userId, Runnable onSaveSuccess) {
        super(parent, "Add New Application", true);
        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel(" Company:")); add(companyField);
        add(new JLabel(" Position:")); add(posField);
        add(new JLabel(" Status:")); add(statusBox);
        add(new JLabel(" Notes:")); add(notesField);

        saveButton.addActionListener(e -> {
            ApplicationDao.addApplicationAsync(userId, companyField.getText(), posField.getText(), 
                (String)statusBox.getSelectedItem(), notesField.getText(), () -> {
                    SwingUtilities.invokeLater(() -> {
                        onSaveSuccess.run();
                        dispose();
                    });
                });
        });
        add(saveButton);
    }
}