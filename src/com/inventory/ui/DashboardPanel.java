package com.inventory.ui;

import com.inventory.model.Product;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class DashboardPanel extends JPanel {

    
    private ProductService  productService  = new ProductService();
    private CategoryService categoryService = new CategoryService();
    private SupplierService supplierService = new SupplierService();

    
    private JLabel totalProductsVal;
    private JLabel totalCategoriesVal;
    private JLabel totalSuppliersVal;
    private JLabel lowStockVal;
    private JLabel totalInventoryVal;

   
    private DefaultTableModel lowStockModel;

   
    public DashboardPanel() {
        
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        buildUI();
        
        refreshData();
    }

    

    private void buildUI() {

        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(AppTheme.CONTENT_BG);

        
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setBackground(AppTheme.CONTENT_BG);
        
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        
        totalProductsVal   = makeBigNumber("0", new Color(59,  130, 246)); // blue
        totalCategoriesVal = makeBigNumber("0", new Color(16,  185, 129)); // green
        totalSuppliersVal  = makeBigNumber("0", new Color(139, 92,  246)); // purple
        lowStockVal        = makeBigNumber("0", new Color(239, 68,  68));  // red

        
        cardsRow.add(buildStatCard("Total Products",   totalProductsVal,   new Color(59,  130, 246)));
        cardsRow.add(buildStatCard("Categories",       totalCategoriesVal, new Color(16,  185, 129)));
        cardsRow.add(buildStatCard("Suppliers",        totalSuppliersVal,  new Color(139, 92,  246)));
        cardsRow.add(buildStatCard("Low Stock Alerts", lowStockVal,        new Color(239, 68,  68)));

        centerPanel.add(cardsRow);
        centerPanel.add(Box.createVerticalStrut(20));

        
        JPanel valueRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valueRow.setBackground(AppTheme.CONTENT_BG);
        valueRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel valueTitleLbl = AppTheme.createLabel("Total Inventory Value:   ");
        valueTitleLbl.setFont(AppTheme.FONT_HEADING);

        totalInventoryVal = new JLabel("₹0.00");
        totalInventoryVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalInventoryVal.setForeground(new Color(16, 185, 129)); // green

        valueRow.add(valueTitleLbl);
        valueRow.add(totalInventoryVal);
        centerPanel.add(valueRow);
        centerPanel.add(Box.createVerticalStrut(24));

        
        JLabel lowTitle = new JLabel("  Products Needing Restock");
        lowTitle.setFont(AppTheme.FONT_HEADING);
        lowTitle.setForeground(AppTheme.TEXT_PRIMARY);
        
        lowTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lowTitle);
        centerPanel.add(Box.createVerticalStrut(8));

        
        String[] cols = {"Product ID", "Product Name", "Current Stock", "Min. Required"};
        lowStockModel = new DefaultTableModel(cols, 0) {
            
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable lowStockTable = new JTable(lowStockModel);
        styleDashboardTable(lowStockTable);

        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);
    }

    
    private JPanel buildStatCard(String title, JLabel valueLabel, Color accent) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 20, 20, 20)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setPreferredSize(new Dimension(0, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(AppTheme.FONT_SMALL);
        titleLbl.setForeground(AppTheme.TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accentBar);
        card.add(Box.createVerticalStrut(16));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private JLabel makeBigNumber(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_STAT_NUMBER);
        lbl.setForeground(color);
        return lbl;
    }

    private void styleDashboardTable(JTable table) {
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);  
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true); 
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);

        table.getTableHeader().setFont(AppTheme.FONT_BUTTON);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false); // prevent column reordering

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, selected, focused, row, col);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? AppTheme.WHITE : AppTheme.TABLE_STRIPE);
                    c.setForeground(AppTheme.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    
    public void refreshData() {

        
        int totalProducts   = productService.getAllProducts().size();
        int totalCategories = categoryService.getAllCategories().size();
        int totalSuppliers  = supplierService.getAllSuppliers().size();
        List<Product> lowStockList = productService.getLowStockProducts();
        int lowCount        = lowStockList.size();
        double totalValue   = productService.getTotalInventoryValue();

       
        totalProductsVal.setText(String.valueOf(totalProducts));
        totalCategoriesVal.setText(String.valueOf(totalCategories));
        totalSuppliersVal.setText(String.valueOf(totalSuppliers));
        lowStockVal.setText(String.valueOf(lowCount));

        
        totalInventoryVal.setText(String.format("₹%,.2f", totalValue));

        
        lowStockModel.setRowCount(0);
        for (Product p : lowStockList) {
            lowStockModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getQuantity(),
                p.getLowStockThreshold()
            });
        }
    }
}