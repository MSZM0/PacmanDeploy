package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PauseScreen extends JPanel {
    public PauseScreen(ActionListener actionListener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel titleLabel = new JLabel("PAUSED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        add(titleLabel, gbc);

        JButton continueButton = createButton("Continue", "CONTINUE");
        continueButton.addActionListener(actionListener);
        add(continueButton, gbc);

        JButton menuButton = createButton("Exit to Menu", "EXIT_TO_MENU");
        menuButton.addActionListener(actionListener);
        add(menuButton, gbc);
    }

    private JButton createButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(new Color(33, 33, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(66, 66, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 33, 255));
            }
        });

        return button;
    }
}
