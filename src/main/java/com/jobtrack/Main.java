package com.jobtrack;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize the database structure first
        DatabaseManager.initializeDatabase();

        // Launch the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            System.out.println("[System] Launching JobTrack Pro UI...");
            new LoginFrame().setVisible(true);
        });
    }
}