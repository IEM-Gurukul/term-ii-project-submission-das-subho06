package com.inventory.ui;

import com.inventory.exception.ValidationException;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * DEDICATED LOW STOCK PANEL
 *
 * This panel ONLY shows products whose quantity is at or below
 * their lowStockThreshold. It gives the store manager a focused
 * view of exactly what needs to be ordered/restocked.
 *
 * WHY A SEPARATE PANEL instead of just using Dashboard?
 * Dashboard shows the COUNT. This panel shows the DETAIL —
 * full product info, supplier contact, and a one-click Restock button.
 * Separation of concerns in the UI layer.
 */
public class LowStockPanel extends JPanel {

    private ProductService  productService  = new ProductService();
    private CategoryService categoryService = new CategoryService();
    private SupplierService supplierService = new SupplierService();

    // Table columns — notice we include Supplier so manager knows who to call
    private static final String[] COLUMNS = {
        "Product ID", "Product Name", "Category",
        "Supplier", "Current Stock", "Min. Required", "Shortage"
    };

    private JTable table;
    private DefaultTableModel tableModel;

    // Summary label at the top — "⚠ 3 products need restocking"
    private JLabel summaryLabel;

    public LowStockPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
    }

    // ─── Build UI ─────────────────────────────────────────────────────────────

    private void buildUI() {

        // ── NORTH: Title + Refresh button ────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.CONTENT_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Left side: title and summary in a vertical stack
        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(AppTheme.CONTENT_BG);

        JLabel titleLabel = new JLabel("  Low Stock Alerts");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.DANGER); // Red title — signals urgency

        // Summary label — updated in refreshTable()
        summaryLabel = new JLabel("Loading...");
        summaryLabel.setFont(AppTheme.FONT_BODY);
        summaryLabel.setForeground(AppTheme.TEXT_SECONDARY);

        titleStack.add(titleLabel);
        titleStack.add(Box.createVerticalStrut(4));
        titleStack.add(summaryLabel);

        header.add(titleStack, BorderLayout.WEST);

        // Right side: Refresh button
        JButton refreshBtn = AppTheme.createButton(
            "  Refresh", new Color(100, 116, 139), Color.WHITE
        );
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        // When clicked, refresh the table to get latest data from files
        refreshBtn.addActionListener(e -> refreshTable());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── CENTER: Info card + Table ─────────────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(AppTheme.CONTENT_BG);

        // Info card explaining what this screen is for
        centerPanel.add(buildInfoCard(), BorderLayout.NORTH);

        // Table
        buildTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        centerPanel.add(scroll, BorderLayout.CENTER);

        // Restock button below table
        centerPanel.add(buildActionBar(), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * An informational banner card at the top of the panel.
     * It explains what "low stock" means using the threshold concept.
     *
     * This is good UX — users don't need to read a manual to
     * understand why a product appears in this list.
     */
    private JPanel buildInfoCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(254, 243, 199)); // warm yellow background
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(202, 138, 4), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel infoLabel = new JLabel(
            "Products below are at or below their minimum stock threshold. " +
            "Click a row and press 'Restock' to update the quantity."
        );
        infoLabel.setFont(AppTheme.FONT_SMALL);
        infoLabel.setForeground(new Color(92, 60, 0)); // dark amber text

        card.add(infoLabel, BorderLayout.CENTER);
        return card;
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // All rows in this table are low-stock, so we use a uniform
        // light-red background — every row is an alert row.
        table.setBackground(AppTheme.LOW_STOCK_BG);
        table.setSelectionBackground(new Color(252, 165, 165)); // deeper red for selected

        table.getTableHeader().setFont(AppTheme.FONT_BUTTON);
        table.getTableHeader().setBackground(new Color(153, 27, 27)); // deep red header
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        int[] widths = {90, 180, 110, 150, 90, 90, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Custom renderer — makes the "Shortage" column bold red
        // to immediately show how urgently restocking is needed
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, selected, focused, row, col);
                if (!selected) {
                    c.setBackground(row % 2 == 0
                        ? AppTheme.LOW_STOCK_BG
                        : new Color(252, 232, 232)); // slightly darker alternate
                    // "Shortage" column (index 6) = bold dark red
                    if (col == 6) {
                        c.setForeground(AppTheme.DANGER);
                        ((JLabel) c).setFont(AppTheme.FONT_BUTTON); // bold
                    } else {
                        c.setForeground(AppTheme.LOW_STOCK_FG);
                        ((JLabel) c).setFont(AppTheme.FONT_BODY);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        // Allow sorting by clicking headers (e.g. sort by shortage amount)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(AppTheme.CONTENT_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton restockBtn = AppTheme.createSuccessButton("  Restock Selected");
        restockBtn.setPreferredSize(new Dimension(160, 36));
        restockBtn.addActionListener(e -> openRestockDialog());

        // Helper label
        JLabel helpLabel = AppTheme.createLabel(
            "  ← Select a product row first"
        );
        helpLabel.setForeground(AppTheme.TEXT_SECONDARY);

        bar.add(restockBtn);
        bar.add(helpLabel);
        return bar;
    }

    // ─── Data loading ─────────────────────────────────────────────────────────

    /**
     * Loads all low-stock products from the service layer.
     * Called by MainFrame when switching to this panel.
     *
     * The "Shortage" column = lowStockThreshold - quantity.
     * Example: threshold=10, quantity=3 → shortage = 7 units needed.
     */
    public void refreshTable() {
        tableModel.setRowCount(0); // clear all rows

        List<Product> lowStockList = productService.getLowStockProducts();

        // Update summary label
        if (lowStockList.isEmpty()) {
            summaryLabel.setText(" All products are sufficiently stocked.");
            summaryLabel.setForeground(AppTheme.SUCCESS);
        } else {
            summaryLabel.setText("  " + lowStockList.size() +
                " product(s) need restocking immediately.");
            summaryLabel.setForeground(AppTheme.DANGER);
        }

        // Populate table
        for (Product p : lowStockList) {
            String categoryName = categoryService.getCategoryById(p.getCategoryId())
                    .map(Category::getName).orElse("Unknown");
            String supplierName = supplierService.getSupplierById(p.getSupplierId())
                    .map(Supplier::getName).orElse("Unknown");

            // Shortage = how many units are MISSING to reach the threshold
            // e.g. threshold=10, qty=3 → need 7 more units
            int shortage = p.getLowStockThreshold() - p.getQuantity();

            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                categoryName,
                supplierName,
                p.getQuantity(),
                p.getLowStockThreshold(),
                "-" + shortage + " units"  // e.g. "-7 units"
            });
        }
    }

    // ─── Restock dialog ───────────────────────────────────────────────────────

    private void openRestockDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please click on a product row first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // table.convertRowIndexToModel() is REQUIRED when a TableRowSorter is active.
        // When the table is sorted, the VISUAL row order differs from the MODEL row order.
        // convertRowIndexToModel() translates the clicked visual row to the actual data row.
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String productId   = (String) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 1);
        int    currentQty  = (int)    tableModel.getValueAt(modelRow, 4);
        int    threshold   = (int)    tableModel.getValueAt(modelRow, 5);

        String input = JOptionPane.showInputDialog(this,
            "Product:       " + productName +
            "\nCurrent Stock: " + currentQty +
            "\nMinimum Required: " + threshold +
            "\n\nEnter new stock quantity:",
            "Restock Product",
            JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int newQty = Integer.parseInt(input.trim());
            productService.updateQuantity(productId, newQty);
            refreshTable(); // refresh this panel

            JOptionPane.showMessageDialog(this,
                productName + " restocked to " + newQty + " units.",
                "Restocked ", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid whole number.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}