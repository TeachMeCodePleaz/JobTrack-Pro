package com.jobtrack.ui;

import com.jobtrack.dao.UserDao;
import com.jobtrack.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced LoginFrame with dynamic state switching between Login and Registration.
 * Handles user input and interfaces with UserDao asynchronously.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton actionButton;
    private JButton switchButton;
    private JLabel titleLabel;
    private boolean isLoginMode = true; // State: true for Login, false for Register

    public LoginFrame() {
        setTitle("JobTrack Pro - Authentication");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        titleLabel = new JLabel("Welcome to JobTrack Pro", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        actionButton = new JButton("Login");
        switchButton = new JButton("No account? Register here");

        actionButton.addActionListener(e -> {
            if (isLoginMode) handleLogin();
            else handleRegister();
        });

        switchButton.addActionListener(e -> toggleMode());

        buttonPanel.add(actionButton);
        buttonPanel.add(switchButton);
        buttonPanel.add(Box.createVerticalStrut(50)); // Spacing
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Login to JobTrack Pro");
            actionButton.setText("Login");
            switchButton.setText("No account? Register here");
        } else {
            titleLabel.setText("Create New Account");
            actionButton.setText("Register Now");
            switchButton.setText("Back to Login");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
            return;
        }

        // Using the async call from Phase 3
        UserDao.registerUser(username, password);
        JOptionPane.showMessageDialog(this, "Registration request submitted. You can now try to login.");
        toggleMode(); // Switch back to login mode automatically
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        actionButton.setEnabled(false);

        UserDao.authenticate(username, password).thenAccept(user -> {
            SwingUtilities.invokeLater(() -> {
                if (user != null) {
                    System.out.println("[Auth] Access granted for user: " + user.getUsername());
                    new MainDashboard(user).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    actionButton.setEnabled(true);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage());
                actionButton.setEnabled(true);
            });
            return null;
        });
    }
}