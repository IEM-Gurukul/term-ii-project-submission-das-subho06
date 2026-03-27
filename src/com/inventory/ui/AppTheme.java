package com.inventory.ui;



import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


 */
public class AppTheme {

    

    // Main accent color (blue — used for primary buttons, active nav items)
    public static final Color PRIMARY           = new Color(37,  99,  235);
    public static final Color PRIMARY_DARK      = new Color(29,  78,  216); // hover state

    // Sidebar (dark navy background)
    public static final Color SIDEBAR_BG        = new Color(15,  23,  42);
    public static final Color SIDEBAR_TEXT      = new Color(148, 163, 184); // muted text
    public static final Color SIDEBAR_ACTIVE_BG = new Color(30,  58,  138); // selected item bg

    // Page background (light grayish-white)
    public static final Color CONTENT_BG        = new Color(241, 245, 249);

    // Pure white — for cards and panels
    public static final Color WHITE             = Color.WHITE;

    // Semantic colors (these communicate meaning like traffic lights)
    public static final Color SUCCESS           = new Color(22,  163, 74);  // green = good
    public static final Color DANGER            = new Color(220, 38,  38);  // red = delete/error
    public static final Color WARNING           = new Color(202, 138, 4);   // yellow = caution

    // Text colors
    public static final Color TEXT_PRIMARY      = new Color(15,  23,  42);  // near-black
    public static final Color TEXT_SECONDARY    = new Color(100, 116, 139); // muted gray

    // Border color (the thin lines around boxes)
    public static final Color BORDER_COLOR      = new Color(226, 232, 240);

    // Table header (dark slate)
    public static final Color TABLE_HEADER_BG   = new Color(51,  65,  85);
    public static final Color TABLE_HEADER_FG   = Color.WHITE;

    // Alternating row stripes in tables (very subtle)
    public static final Color TABLE_STRIPE      = new Color(248, 250, 252);

    // Low stock row highlight (light red background, dark red text)
    public static final Color LOW_STOCK_BG      = new Color(254, 242, 242);
    public static final Color LOW_STOCK_FG      = new Color(185, 28,  28);

    
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING      = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SIDEBAR      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_STAT_NUMBER  = new Font("Segoe UI", Font.BOLD,  28);

    
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, Color.WHITE);
    }

    
    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE);
    }

    
    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, Color.WHITE);
    }

    
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
}