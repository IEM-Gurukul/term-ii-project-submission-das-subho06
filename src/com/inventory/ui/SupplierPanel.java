package com.inventory.ui;

import com.inventory.exception.ValidationException;
import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;


public class SupplierPanel extends JPanel {

    private SupplierService supplierService = new SupplierService();

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS = {
        "ID", "Company Name", "Contact Person", "Phone", "Email"
    };

    public SupplierPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
    }

    private void buildUI() {

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.CONTENT_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Suppliers");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton addBtn = AppTheme.createPrimaryButton("＋ Add Supplier");
        addBtn.setPreferredSize(new Dimension(150, 36));
        addBtn.addActionListener(e -> openAddDialog());
        header.add(addBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        // Action buttons
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBar.setBackground(AppTheme.CONTENT_BG);
        actionBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton editBtn   = AppTheme.createButton("✏  Edit",   new Color(59, 130, 246), Color.WHITE);
        JButton deleteBtn = AppTheme.createDangerButton("✕  Delete");
        editBtn.setPreferredSize(new Dimension(110, 34));
        deleteBtn.setPreferredSize(new Dimension(110, 34));

        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());

        actionBar.add(editBtn);
        actionBar.add(deleteBtn);

        add(scroll, BorderLayout.CENTER);
        add(actionBar, BorderLayout.SOUTH);
    }

    private void styleTable() {
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(AppTheme.FONT_BUTTON);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {70, 180, 150, 120, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? AppTheme.WHITE : AppTheme.TABLE_STRIPE);
                    c.setForeground(AppTheme.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Supplier s : supplierService.getAllSuppliers()) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getContactName(), s.getPhone(), s.getEmail()
            });
        }
    }

    private String getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a supplier first.", "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void openAddDialog() {
        JTextField[] fields = createFormFields();
        JPanel form = buildForm(fields, null);

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                supplierService.addSupplier(
                    fields[0].getText(), fields[1].getText(),
                    fields[2].getText(), fields[3].getText(), fields[4].getText()
                );
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Supplier added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditDialog() {
        String id = getSelectedId();
        if (id == null) return;

        Optional<Supplier> opt = supplierService.getSupplierById(id);
        if (!opt.isPresent()) return;
        Supplier s = opt.get();

        JTextField[] fields = createFormFields();
        
        fields[0].setText(s.getName());
        fields[1].setText(s.getContactName());
        fields[2].setText(s.getPhone());
        fields[3].setText(s.getEmail());
        fields[4].setText(s.getAddress());

        JPanel form = buildForm(fields, s);

        int result = JOptionPane.showConfirmDialog(this, form,
            "Edit Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                supplierService.updateSupplier(
                    id, fields[0].getText(), fields[1].getText(),
                    fields[2].getText(), fields[3].getText(), fields[4].getText()
                );
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Supplier updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextField[] createFormFields() {
        
        JTextField[] fields = new JTextField[5];
        for (int i = 0; i < 5; i++) {
            fields[i] = AppTheme.createTextField();
            fields[i].setPreferredSize(new Dimension(230, 30));
        }
        return fields;
    }

    private JPanel buildForm(JTextField[] fields, Supplier existing) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.setPreferredSize(new Dimension(400, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        String[] labels = {
            "Company Name *", "Contact Person", "Phone *", "Email", "Address"
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(AppTheme.createLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        return form;
    }

    private void deleteSelected() {
        String id = getSelectedId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this supplier?\nNote: suppliers linked to products cannot be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                supplierService.deleteSupplier(id);
                refreshTable();
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}