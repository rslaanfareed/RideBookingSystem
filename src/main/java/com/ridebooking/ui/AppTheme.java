package com.ridebooking.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class AppTheme {
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(24, 33, 45);
    public static final Color MUTED_TEXT = new Color(94, 108, 132);
    public static final Color PRIMARY = new Color(0, 132, 137);
    public static final Color PRIMARY_DARK = new Color(0, 96, 100);
    public static final Color ACCENT = new Color(255, 193, 7);
    public static final Color SUCCESS = new Color(46, 125, 50);
    public static final Color DANGER = new Color(198, 40, 40);
    public static final Color BORDER = new Color(224, 229, 235);

    private static final String FONT_FAMILY = "Segoe UI";

    private AppTheme() {
    }

    public static Font font(int size, int style) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static JPanel page() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        return panel;
    }

    public static JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_DARK);
        header.setPreferredSize(new Dimension(0, 82));
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(font(24, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(font(13, Font.PLAIN));
        subtitleLabel.setForeground(new Color(213, 244, 245));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    public static JPanel card() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());
        return card;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(font(size, Font.BOLD));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel body(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(13, Font.PLAIN));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    public static JButton primaryButton(String text) {
        return button(text, PRIMARY, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, new Color(234, 241, 242), PRIMARY_DARK);
    }

    public static JButton dangerButton(String text) {
        return button(text, DANGER, Color.WHITE);
    }

    public static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(font(13, Font.BOLD));
        button.setPreferredSize(new Dimension(150, 40));
        button.setMaximumSize(new Dimension(220, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void styleField(JComponent field) {
        field.setFont(font(13, Font.PLAIN));
        field.setMaximumSize(new Dimension(420, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    public static JTextArea readOnlyArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(font(13, Font.PLAIN));
        area.setForeground(TEXT);
        area.setBackground(SURFACE);
        return area;
    }
}
