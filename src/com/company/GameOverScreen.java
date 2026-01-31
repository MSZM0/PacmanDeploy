package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameOverScreen extends JPanel {
    private JButton menuButton;
    private JTextField nicknameField;
    private boolean isWin;
    private int finalScore;
    private ActionListener listener;

    public GameOverScreen(boolean isWin, int finalScore, ActionListener listener) {
        this.isWin = isWin;
        this.finalScore = finalScore;
        this.listener = listener;

        setLayout(null);
        setBackground(new Color(0, 0, 0));
        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
            GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE));

        JLabel titleLabel = new JLabel(isWin ? "YOU WIN!" : "GAME OVER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 70));
        titleLabel.setForeground(isWin ? Color.GREEN : Color.RED);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 80, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE, 120);
        add(titleLabel);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBounds(0, 220, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE, 60);
        add(scoreLabel);

        JLabel nickLabel = new JLabel("Enter your nickname:");
        nickLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nickLabel.setForeground(Color.WHITE);
        nickLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nickLabel.setBounds(0, 290, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE, 30);
        add(nickLabel);

        nicknameField = new JTextField();
        nicknameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        nicknameField.setBackground(new Color(50, 50, 50));
        nicknameField.setForeground(Color.WHITE);
        nicknameField.setCaretColor(Color.WHITE);
        nicknameField.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 120, 325, 240, 35);
        add(nicknameField);

        menuButton = new JButton("Save & Return to Menu");
        menuButton.setActionCommand("SAVE_SCORE");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 18));
        menuButton.setFocusPainted(false);
        menuButton.setBackground(new Color(0, 100, 255));
        menuButton.setForeground(Color.WHITE);
        menuButton.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 120, 380, 240, 50);
        menuButton.addActionListener(e -> saveAndReturnToMenu());
        add(menuButton);
    }

    private void saveAndReturnToMenu() {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            nickname = "Player";
        }
        String resultType = isWin ? "[WIN]" : "[LOSS]";
        Scoreboard.saveScore(nickname + " " + resultType + " - " + finalScore);
        listener.actionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "BACK_TO_MENU"));
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
