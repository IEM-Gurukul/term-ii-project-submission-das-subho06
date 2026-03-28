package com.inventory.ui;

import com.inventory.exception.ValidationException;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class ProductPanel extends JPanel {

    private ProductService  productService  = new ProductService();
    private CategoryService categoryService = new CategoryService();
    private SupplierService supplierService = new SupplierService();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Category> categoryFilter;

    
    private static final String[] COLUMNS = {
        "ID", "Name", "Category", "Supplier", "Stock", "Price (₹)", "Status"
    };

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
    }


    private void buildUI() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppTheme.CONTENT_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Products");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton addBtn = AppTheme.createPrimaryButton("＋ Add Product");
        addBtn.setPreferredSize(new Dimension(140, 36));
        addBtn.addActionListener(e -> openAddDialog());
        headerPanel.add(addBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(AppTheme.CONTENT_BG);

        centerPanel.add(buildFilterBar(), BorderLayout.NORTH);

        buildTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(buildActionBar(), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBackground(AppTheme.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Search field
        searchField = AppTheme.createTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search products by name");

        JButton searchBtn = AppTheme.createPrimaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(85, 30));
        searchBtn.addActionListener(e -> performSearch());

        
        categoryFilter = new JComboBox<>();
        categoryFilter.setFont(AppTheme.FONT_BODY);
        categoryFilter.setPreferredSize(new Dimension(160, 30));
        categoryFilter.addActionListener(e -> performSearch());

        JButton resetBtn = AppTheme.createButton("Reset", new Color(100, 116, 139), Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(75, 30));
        resetBtn.addActionListener(e -> resetFilters());

        bar.add(AppTheme.createLabel("Search:"));
        bar.add(searchField);
        bar.add(searchBtn);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(AppTheme.createLabel("Category:"));
        bar.add(categoryFilter);
        bar.add(resetBtn);

        return bar;
    }

    private void buildTable() {
        
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);

        table.getTableHeader().setFont(AppTheme.FONT_BUTTON);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {70, 200, 120, 140, 60, 90, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focused, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        t, value, selected, focused, row, col);

                if (!selected) {
                    String status = (String) t.getModel().getValueAt(row, 6);
                    boolean isLow = "⚠ Low".equals(status);

                    if (isLow) {
                        c.setBackground(AppTheme.LOW_STOCK_BG);
                        c.setForeground(AppTheme.LOW_STOCK_FG);
                    } else {
                        c.setBackground(row % 2 == 0 ? AppTheme.WHITE : AppTheme.TABLE_STRIPE);
                        c.setForeground(AppTheme.TEXT_PRIMARY);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(AppTheme.CONTENT_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JButton editBtn    = AppTheme.createButton("✏  Edit",    new Color(59, 130, 246), Color.WHITE);
        JButton deleteBtn  = AppTheme.createDangerButton("✕  Delete");
        JButton restockBtn = AppTheme.createSuccessButton("↑  Restock");

        editBtn.setPreferredSize(new Dimension(110, 34));
        deleteBtn.setPreferredSize(new Dimension(110, 34));
        restockBtn.setPreferredSize(new Dimension(110, 34));

        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        restockBtn.addActionListener(e -> openRestockDialog());

        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(restockBtn);
        return bar;
    }

    
    public void refreshTable() {
        reloadCategoryFilter();
        populateTable(productService.getAllProducts());
    }

    private void reloadCategoryFilter() {
        Category allOption = new Category("ALL", "All Categories", "");
        categoryFilter.removeAllItems();
        categoryFilter.addItem(allOption);
        for (Category c : categoryService.getAllCategories()) {
            categoryFilter.addItem(c);
        }
    }

    
    private void populateTable(List<Product> products) {
        tableModel.setRowCount(0); // clear all rows first

        for (Product p : products) {
            
            String categoryName = categoryService.getCategoryById(p.getCategoryId())
                    .map(Category::getName)
                    .orElse("Unknown");

            String supplierName = supplierService.getSupplierById(p.getSupplierId())
                    .map(Supplier::getName)
                    .orElse("Unknown");

            String status = p.isLowStock() ? " Low" : " OK";

            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                categoryName,
                supplierName,
                p.getQuantity(),
                String.format("%.2f", p.getPrice()),
                status
            });
        }
    }


    private void performSearch() {
        String keyword = searchField.getText().trim();
        Category selectedCat = (Category) categoryFilter.getSelectedItem();

        // Start with name search (or all products if keyword empty)
        List<Product> results = keyword.isEmpty()
                ? productService.getAllProducts()
                : productService.searchByName(keyword);

        // Then apply category filter if not "All Categories"
        if (selectedCat != null && !"ALL".equals(selectedCat.getId())) {
            final String catId = selectedCat.getId();
            results = results.stream()
                    .filter(p -> p.getCategoryId().equals(catId))
                    .collect(Collectors.toList());
        }

        populateTable(results);
    }

    private void resetFilters() {
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        refreshTable();
    }

    
    private String getSelectedId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please click on a product in the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // Column 0 = ID column
        return (String) tableModel.getValueAt(selectedRow, 0);
    }


    private void openAddDialog() {
        
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Add New Product", true
        );
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(AppTheme.WHITE);

        JPanel form = buildProductForm(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(AppTheme.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        JButton cancelBtn = AppTheme.createButton("Cancel", new Color(100, 116, 139), Color.WHITE);
        JButton saveBtn   = AppTheme.createPrimaryButton("Save");
        cancelBtn.setPreferredSize(new Dimension(90, 34));
        saveBtn.setPreferredSize(new Dimension(90, 34));

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                extractAndSaveProduct(form);
                dialog.dispose();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Product added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Price, cost, quantity, and threshold must be valid numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true); // this BLOCKS until dialog is closed (because modal=true)
    }


    private void openEditDialog() {
        String id = getSelectedId();
        if (id == null) return;

        Optional<Product> opt = productService.getProductById(id);
        if (!opt.isPresent()) {
            JOptionPane.showMessageDialog(this,
                "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Product existing = opt.get();

        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Edit Product — " + existing.getName(), true
        );
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(AppTheme.WHITE);

        JPanel form = buildProductForm(existing);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(AppTheme.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        JButton cancelBtn = AppTheme.createButton("Cancel", new Color(100, 116, 139), Color.WHITE);
        JButton updateBtn = AppTheme.createPrimaryButton("Update");
        cancelBtn.setPreferredSize(new Dimension(90, 34));
        updateBtn.setPreferredSize(new Dimension(90, 34));

        cancelBtn.addActionListener(e -> dialog.dispose());

        updateBtn.addActionListener(e -> {
            try {
                extractAndUpdateProduct(id, form);
                dialog.dispose();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Product updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Price, cost, quantity, and threshold must be valid numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(updateBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    
    private JPanel buildProductForm(Product existing) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6); // padding around each cell

        JTextField nameField  = AppTheme.createTextField();
        JTextField descField  = AppTheme.createTextField();
        JTextField priceField = AppTheme.createTextField();
        JTextField costField  = AppTheme.createTextField();
        JTextField qtyField   = AppTheme.createTextField();
        JTextField thrField   = AppTheme.createTextField();

        JComboBox<Category> catCombo = new JComboBox<>();
        JComboBox<Supplier> supCombo = new JComboBox<>();
        catCombo.setFont(AppTheme.FONT_BODY);
        supCombo.setFont(AppTheme.FONT_BODY);

        for (Category c : categoryService.getAllCategories()) catCombo.addItem(c);
        for (Supplier s : supplierService.getAllSuppliers())   supCombo.addItem(s);

        if (existing != null) {
            nameField.setText(existing.getName());
            descField.setText(existing.getDescription());
            priceField.setText(String.valueOf(existing.getPrice()));
            costField.setText(String.valueOf(existing.getCostPrice()));
            qtyField.setText(String.valueOf(existing.getQuantity()));
            thrField.setText(String.valueOf(existing.getLowStockThreshold()));

            for (int i = 0; i < catCombo.getItemCount(); i++) {
                if (catCombo.getItemAt(i).getId().equals(existing.getCategoryId())) {
                    catCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < supCombo.getItemCount(); i++) {
                if (supCombo.getItemAt(i).getId().equals(existing.getSupplierId())) {
                    supCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            qtyField.setText("0");
            thrField.setText("5");
        }

        form.putClientProperty("nameField",  nameField);
        form.putClientProperty("descField",  descField);
        form.putClientProperty("priceField", priceField);
        form.putClientProperty("costField",  costField);
        form.putClientProperty("qtyField",   qtyField);
        form.putClientProperty("thrField",   thrField);
        form.putClientProperty("catCombo",   catCombo);
        form.putClientProperty("supCombo",   supCombo);

        Object[][] rows = {
            {"Product Name *",       nameField},
            {"Description",          descField},
            {"Selling Price (₹) *",  priceField},
            {"Cost Price (₹)",       costField},
            {"Quantity *",           qtyField},
            {"Low Stock Threshold",  thrField},
            {"Category *",           catCombo},
            {"Supplier *",           supCombo}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            form.add(AppTheme.createLabel((String) rows[i][0]), gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add((Component) rows[i][1], gbc);
        }

        return form;
    }


    private void extractAndSaveProduct(JPanel form) {
        String   name  = getField(form, "nameField");
        String   desc  = getField(form, "descField");
        double   price = Double.parseDouble(getField(form, "priceField"));
        double   cost  = Double.parseDouble(getField(form, "costField"));
        int      qty   = Integer.parseInt(getField(form, "qtyField"));
        int      thr   = Integer.parseInt(getField(form, "thrField"));
        Category cat   = (Category) ((JComboBox<?>) form.getClientProperty("catCombo")).getSelectedItem();
        Supplier sup   = (Supplier) ((JComboBox<?>) form.getClientProperty("supCombo")).getSelectedItem();

        if (cat == null || sup == null) {
            throw new ValidationException(
                "Please add at least one Category and Supplier before adding products.");
        }

        productService.addProduct(name, desc, price, cost, qty, thr,
                cat.getId(), sup.getId());
    }

    private void extractAndUpdateProduct(String id, JPanel form) {
        String   name  = getField(form, "nameField");
        String   desc  = getField(form, "descField");
        double   price = Double.parseDouble(getField(form, "priceField"));
        double   cost  = Double.parseDouble(getField(form, "costField"));
        int      qty   = Integer.parseInt(getField(form, "qtyField"));
        int      thr   = Integer.parseInt(getField(form, "thrField"));
        Category cat   = (Category) ((JComboBox<?>) form.getClientProperty("catCombo")).getSelectedItem();
        Supplier sup   = (Supplier) ((JComboBox<?>) form.getClientProperty("supCombo")).getSelectedItem();

        if (cat == null || sup == null) {
            throw new ValidationException("Category and Supplier cannot be empty.");
        }

        productService.updateProduct(id, name, desc, price, cost, qty, thr,
                cat.getId(), sup.getId());
    }

    private String getField(JPanel form, String key) {
        return ((JTextField) form.getClientProperty(key)).getText().trim();
    }


    private void deleteSelected() {
        String id = getSelectedId();
        if (id == null) return;

     
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this product?\nThis cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(id);
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Product deleted.", "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // RESTOCK 

    private void openRestockDialog() {
        String id = getSelectedId();
        if (id == null) return;

        Optional<Product> opt = productService.getProductById(id);
        if (!opt.isPresent()) return;
        Product p = opt.get();

        // showInputDialog shows a dialog with a text field.
        // Returns the text the user typed, or null if they cancelled.
        String input = JOptionPane.showInputDialog(this,
            "Product: " + p.getName()
            + "\nCurrent stock: " + p.getQuantity()
            + "\n\nEnter new quantity:",
            "Restock Product",
            JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int newQty = Integer.parseInt(input.trim());
            productService.updateQuantity(id, newQty);
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Stock updated to " + newQty + " units.", "Restocked",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a whole number.", "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}