package com.inventory;

import com.inventory.ui.MainFrame;
import javax.swing.*;


public class Main {

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {

            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error occurred:\n\n"
                        + throwable.getClass().getSimpleName()
                        + ": " + throwable.getMessage()
                        + "\n\nThe application will try to continue.",
                    "Unexpected Error",
                    JOptionPane.ERROR_MESSAGE
                );
                // Print to console for debugging during development
                throwable.printStackTrace();
            });
        });
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            
            System.out.println("Could not set system look and feel: " + e.getMessage());
        }

        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true); 
        });
    }
}