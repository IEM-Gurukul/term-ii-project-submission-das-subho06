package com.inventory.ui;


import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;   


    private DashboardPanel  dashboardPanel;
    private ProductPanel    productPanel;
    private CategoryPanel   categoryPanel;
    private SupplierPanel   supplierPanel;

    private JButton activeButton;


    public MainFrame() {
        setupFrame();      
        buildUI();         
        dashboardPanel.refreshData();
    }


    private void setupFrame() {
        setTitle("Inventory Management System");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(1200, 700);


        setMinimumSize(new Dimension(900, 600));

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
    }


    private void buildUI() {

        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.CONTENT_BG);

        dashboardPanel = new DashboardPanel();
        productPanel   = new ProductPanel();
        categoryPanel  = new CategoryPanel();
        supplierPanel  = new SupplierPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(productPanel,   "PRODUCTS");
        contentPanel.add(categoryPanel,  "CATEGORIES");
        contentPanel.add(supplierPanel,  "SUPPLIERS");

        add(contentPanel, BorderLayout.CENTER);
    }


    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppTheme.SIDEBAR_BG);

        sidebar.setPreferredSize(new Dimension(220, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(AppTheme.SIDEBAR_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        titlePanel.setMaximumSize(new Dimension(220, 80));

        JLabel titleLabel = new JLabel("📦 InvenTrack");
        titleLabel.setFont(AppTheme.FONT_SIDEBAR_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        sidebar.add(titlePanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 41, 59));
        sep.setBackground(new Color(30, 41, 59));
        sep.setMaximumSize(new Dimension(220, 1));
        sidebar.add(sep);

        sidebar.add(Box.createVerticalStrut(16));

        JButton dashBtn     = buildNavButton("🏠  Dashboard",  "DASHBOARD");
        JButton productBtn  = buildNavButton("📦  Products",   "PRODUCTS");
        JButton categoryBtn = buildNavButton("🗂  Categories", "CATEGORIES");
        JButton supplierBtn = buildNavButton("🚚  Suppliers",  "SUPPLIERS");

        markActive(dashBtn);
        activeButton = dashBtn;

        sidebar.add(dashBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(productBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(categoryBtn);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(supplierBtn);

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    
    private JButton buildNavButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(AppTheme.FONT_SIDEBAR);
        btn.setForeground(AppTheme.SIDEBAR_TEXT);
        btn.setBackground(AppTheme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setMaximumSize(new Dimension(220, 46));
        btn.setPreferredSize(new Dimension(220, 46));

        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addActionListener(e -> switchToPanel(panelName, btn));

        return btn;
    }

    
    private void switchToPanel(String panelName, JButton clickedBtn) {

        
        if (activeButton != null) {
            activeButton.setBackground(AppTheme.SIDEBAR_BG);
            activeButton.setForeground(AppTheme.SIDEBAR_TEXT);
        }

        
        markActive(clickedBtn);
        activeButton = clickedBtn;

        
        switch (panelName) {
            case "DASHBOARD":   dashboardPanel.refreshData();   break;
            case "PRODUCTS":    productPanel.refreshTable();    break;
            case "CATEGORIES":  categoryPanel.refreshTable();   break;
            case "SUPPLIERS":   supplierPanel.refreshTable();   break;
        }

        
        cardLayout.show(contentPanel, panelName);
    }

    
    private void markActive(JButton btn) {
        btn.setBackground(AppTheme.SIDEBAR_ACTIVE_BG);
        btn.setForeground(Color.WHITE);
    }
}