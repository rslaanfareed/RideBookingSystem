package com.ridebooking.util;

import javax.swing.*;
import java.util.Optional;

public class NotificationService {
    public static void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static Optional<String> showInputDialog(String title, String headerText, String contentText) {
        String result = JOptionPane.showInputDialog(null, contentText, title, JOptionPane.QUESTION_MESSAGE);
        return Optional.ofNullable(result);
    }
}
