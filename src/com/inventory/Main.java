package com.inventory;

import com.inventory.ui.MainFrame;
import javax.swing.*;


public class Main {

    public static void main(String[] args) {

        
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