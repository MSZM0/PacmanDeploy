package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Panel extends JPanel implements KeyListener {
    private final GameEngine engine;
    private final TextureManager textureManager;
    private final GameRenderer renderer;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private int currentScore;
    private int currentLives;

    public Panel(GameEngine engine) {
        this.engine = engine;
        this.textureManager = new TextureManager();
        this.renderer = new GameRenderer(textureManager);

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(
            GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
            GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE
        ));

        initializeUI();
    }

    private void initializeUI() {
        setLayout(null);

        int screenWidth = GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE;

        scoreLabel = new JLabel("0");
        int scoreLabelWidth = 200;
        scoreLabel.setBounds(screenWidth / 2 - scoreLabelWidth / 2, 10, scoreLabelWidth, 30);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        add(scoreLabel);

        livesLabel = new JLabel("♥ x3");
        int livesLabelWidth = 150;
        livesLabel.setBounds(screenWidth / 2 - livesLabelWidth / 2, 45, livesLabelWidth, 30);
        livesLabel.setForeground(Color.RED);
        livesLabel.setHorizontalAlignment(JLabel.CENTER);
        livesLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        add(livesLabel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, engine);
        updateLabels();
    }

    private void updateLabels() {
        int score = engine.getScore();
        int lives = engine.getLives();

        if (score != currentScore) {
            scoreLabel.setText(Integer.toString(score));
            currentScore = score;
        }
        if (lives != currentLives) {
            livesLabel.setText("♥ x" + lives);
            currentLives = lives;
        }
    }

    public void updateScore(int score) {
        scoreLabel.setText(Integer.toString(score));
    }

    public void updateLives(int lives) {
        livesLabel.setText("♥ x" + lives);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && engine.getCurrentState() == GameStateType.PLAYING) {
            engine.pauseGame();
            return;
        }

        if (engine.getCurrentState() != GameStateType.PLAYING) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_D -> engine.getPacman().setNextDirection(Direction.RIGHT);
            case KeyEvent.VK_A -> engine.getPacman().setNextDirection(Direction.LEFT);
            case KeyEvent.VK_W -> engine.getPacman().setNextDirection(Direction.UP);
            case KeyEvent.VK_S -> engine.getPacman().setNextDirection(Direction.DOWN);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
