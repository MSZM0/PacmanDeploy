package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private JButton startGameButton;
    private JButton scoreboardButton;
    private JButton exitButton;

    public MainMenu(ActionListener listener) {
        setLayout(null);
        setBackground(new Color(0, 0, 0));
        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
            GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE));

        JLabel titleLabel = new JLabel("PAC-MAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 80, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE, 100);
        add(titleLabel);

        startGameButton = new JButton("Start Game");
        startGameButton.setActionCommand("START");
        startGameButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startGameButton.setFocusPainted(false);
        startGameButton.setBackground(new Color(0, 100, 255));
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 100, 250, 200, 50);
        startGameButton.addActionListener(listener);
        add(startGameButton);

        scoreboardButton = new JButton("Scoreboard");
        scoreboardButton.setActionCommand("SCOREBOARD");
        scoreboardButton.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreboardButton.setFocusPainted(false);
        scoreboardButton.setBackground(new Color(0, 150, 0));
        scoreboardButton.setForeground(Color.WHITE);
        scoreboardButton.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 100, 330, 200, 50);
        scoreboardButton.addActionListener(listener);
        add(scoreboardButton);

        exitButton = new JButton("Exit");
        exitButton.setActionCommand("EXIT");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 20));
        exitButton.setFocusPainted(false);
        exitButton.setBackground(new Color(200, 0, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 100, 410, 200, 50);
        exitButton.addActionListener(listener);
        add(exitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(200, 0, 0));
        g.fillRect(0, 0, getWidth(), 5);
        g.setColor(new Color(255, 200, 0));
        g.fillRect(0, 5, getWidth(), 5);
        g.setColor(new Color(0, 200, 0));
        g.fillRect(0, 10, getWidth(), 5);
        g.setColor(new Color(0, 0, 200));
        g.fillRect(0, 15, getWidth(), 5);
    }
}
