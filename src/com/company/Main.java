package com.company;

import javax.swing.*;
import java.awt.*;

public class Main implements GameListener {
    private static GameEngine engine;
    private static JFrame gameWindow;
    private static Panel panel;
    private static MainMenu mainMenu;
    private static Scoreboard scoreboard;
    private static GameOverScreen gameOverScreen;
    private static PauseScreen pauseScreen;
    private static JLayeredPane layeredPane;

    public static void main(String[] args) {
        engine = new GameEngine();
        engine.initializeGame();
        engine.addListener(new Main());

        createUI();
        startGameLoop();
    }

    private static void createUI() {
        gameWindow = new JFrame();
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setTitle("Pac-Man");
        gameWindow.setResizable(false);

        showMainMenu();
    }

    private static void showMainMenu() {
        mainMenu = new MainMenu(event -> handleMenuAction(event.getActionCommand()));
        SwingUtilities.invokeLater(() -> {
            gameWindow.getContentPane().removeAll();
            gameWindow.add(mainMenu);
            gameWindow.pack();
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);
        });
    }

    private static void handleMenuAction(String command) {
        switch (command) {
            case "START" -> startGame();
            case "SCOREBOARD" -> showScoreboard();
            case "EXIT" -> System.exit(0);
            case "BACK", "BACK_TO_MENU" -> showMainMenu();
            case "CONTINUE" -> resumeGame();
            case "EXIT_TO_MENU" -> exitToMenu();
        }
    }

    private static void startGame() {
        engine.resetGameState();
        panel = new Panel(engine);

        SwingUtilities.invokeLater(() -> {
            gameWindow.getContentPane().removeAll();

            layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(
                GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
                GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE
            ));

            panel.setBounds(0, 0,
                GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
                GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE);
            layeredPane.add(panel, JLayeredPane.DEFAULT_LAYER);

            gameWindow.add(layeredPane);
            gameWindow.pack();
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);

            panel.setFocusable(true);
            panel.requestFocus();

            engine.setCurrentState(GameStateType.PLAYING);
            engine.startGameWithDelay();
        });
    }

    private static void showScoreboard() {
        scoreboard = new Scoreboard(event -> handleMenuAction(event.getActionCommand()));
        SwingUtilities.invokeLater(() -> {
            gameWindow.getContentPane().removeAll();
            gameWindow.add(scoreboard);
            gameWindow.revalidate();
            gameWindow.repaint();
        });
    }

    private static void resumeGame() {
        SwingUtilities.invokeLater(() -> {
            if (pauseScreen != null && layeredPane != null) {
                layeredPane.remove(pauseScreen);
                pauseScreen = null;
                layeredPane.revalidate();
                layeredPane.repaint();

                panel.setFocusable(true);
                panel.requestFocus();

                engine.resumeGame();
            }
        });
    }

    private static void exitToMenu() {
        SwingUtilities.invokeLater(() -> {
            if (pauseScreen != null && layeredPane != null) {
                layeredPane.remove(pauseScreen);
                pauseScreen = null;
            }
            engine.setCurrentState(GameStateType.MENU);
            showMainMenu();
        });
    }

    private static void startGameLoop() {
        new Thread(() -> {
            GameStateType previousState = GameStateType.MENU;

            while (true) {
                GameStateType currentState = engine.getCurrentState();

                if (currentState == GameStateType.PAUSED && previousState == GameStateType.PLAYING) {
                    SwingUtilities.invokeLater(() -> showPauseScreen());
                }

                if (currentState == GameStateType.PLAYING) {
                    engine.update();
                    engine.checkWinCondition();
                    if (panel != null) {
                        panel.repaint();
                    }
                }

                previousState = currentState;

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private static void showPauseScreen() {
        if (layeredPane != null && pauseScreen == null) {
            pauseScreen = new PauseScreen(event -> handleMenuAction(event.getActionCommand()));
            pauseScreen.setBounds(0, 0,
                GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
                GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE);
            layeredPane.add(pauseScreen, JLayeredPane.PALETTE_LAYER);
            layeredPane.revalidate();
            layeredPane.repaint();
        }
    }

    @Override
    public void onGameStateChanged(GameStateType state) {
        if (state == GameStateType.GAME_OVER || state == GameStateType.WIN) {
            int finalScore = engine.getScore();
            boolean isWin = state == GameStateType.WIN;

            SwingUtilities.invokeLater(() -> {
                gameOverScreen = new GameOverScreen(isWin, finalScore,
                    event -> handleMenuAction(event.getActionCommand()));
                gameWindow.getContentPane().removeAll();
                gameWindow.add(gameOverScreen);
                gameWindow.revalidate();
                gameWindow.repaint();
            });
        }
    }

    @Override
    public void onScoreChanged(int newScore) {
        if (panel != null) {
            panel.updateScore(newScore);
        }
    }

    @Override
    public void onLivesChanged(int livesRemaining) {
        if (panel != null) {
            panel.updateLives(livesRemaining);
        }
    }
}
