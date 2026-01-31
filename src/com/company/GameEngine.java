package com.company;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameEngine {
    private final BoardManager boardManager;
    private final ArrayList<Ghost> ghosts;
    private final ArrayList<FloatingText> floatingTexts;
    private PacMan pacman;
    private int score;
    private int lives;
    private int ghostMultiplier;
    private GameStateType currentState;
    private final CollisionHandlerImpl collisionHandler;
    private final ArrayList<GameListener> listeners;

    private Timer powerUpTimer;
    private long powerUpRemainingTime;
    private long powerUpPauseTime;

    private boolean isStartDelay;
    private long startDelayEndTime;
    private long startDelayRemainingTime;

    public GameEngine() {
        this.boardManager = new BoardManager(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);
        this.ghosts = new ArrayList<>();
        this.floatingTexts = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.collisionHandler = new CollisionHandlerImpl();
        this.currentState = GameStateType.MENU;
        this.powerUpRemainingTime = 0;
        this.powerUpPauseTime = 0;
        this.isStartDelay = false;
        this.startDelayEndTime = 0;
        this.startDelayRemainingTime = 0;
    }

    public void initializeGame() {
        try {
            boardManager.loadFromFile("Board.txt");
            boardManager.buildBoard();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
        resetGameState();
    }

    public void resetGameState() {
        score = 0;
        lives = GameConstants.INITIAL_LIVES;
        ghostMultiplier = 1;

        boardManager.buildBoard();

        pacman = new PacMan(
            GameConstants.PACMAN_START_X * GameConstants.TILE_SIZE,
            GameConstants.PACMAN_START_Y * GameConstants.TILE_SIZE,
            boardManager.getBoard()
        );
        ghosts.clear();
        createGhosts();
        floatingTexts.clear();

        if (powerUpTimer != null) {
            powerUpTimer.cancel();
            powerUpTimer = null;
        }
        powerUpRemainingTime = 0;
        powerUpPauseTime = 0;

        notifyScoreChanged(score);
        notifyLivesChanged(lives);
    }

    public void startGameWithDelay() {
        isStartDelay = true;
        startDelayEndTime = System.currentTimeMillis() + GameConstants.START_DELAY_DURATION;
    }

    private void createGhosts() {
        ghosts.add(new Ghost(Ghost.GhostType.RED, boardManager.getBoard()));
        ghosts.add(new Ghost(Ghost.GhostType.ORANGE, boardManager.getBoard()));
        ghosts.add(new Ghost(Ghost.GhostType.BLUE, boardManager.getBoard()));
        ghosts.add(new Ghost(Ghost.GhostType.PINK, boardManager.getBoard()));
    }

    public void update() {
        if (currentState != GameStateType.PLAYING) return;

        if (isStartDelay) {
            if (System.currentTimeMillis() >= startDelayEndTime) {
                isStartDelay = false;
            } else {
                updateFloatingTexts();
                return;
            }
        }

        Ghost.moveGhosts(ghosts, pacman, boardManager.getBoard());
        pacman.move();
        handlePacmanFieldContent();
        collisionHandler.checkCollisions(this);
        updateFloatingTexts();
    }

    private void handlePacmanFieldContent() {
        Field currentField = boardManager.getBoard()[pacman.getXGrid()][pacman.getYGrid()];
        int content = currentField.eatContent();

        if (content == 1) {
            addScore(10);
            boardManager.decrementDotCount();
        } else if (content == 2) {
            addScore(50);
            boardManager.decrementDotCount();
            activatePowerUp();
        }
    }

    public void activatePowerUp() {
        ghostMultiplier = 1;
        for (Ghost ghost : ghosts) {
            if (ghost.getState() != Ghost.State.EATEN) {
                ghost.setState(Ghost.State.FRIGHTENED);
            }
        }

        if (powerUpTimer != null) {
            powerUpTimer.cancel();
        }

        powerUpRemainingTime = GameConstants.POWER_UP_DURATION;
        powerUpPauseTime = System.currentTimeMillis();

        powerUpTimer = new Timer();
        powerUpTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivatePowerUp();
            }
        }, GameConstants.POWER_UP_DURATION);
    }

    private void deactivatePowerUp() {
        for (Ghost ghost : ghosts) {
            if (ghost.getState() != Ghost.State.EATEN) {
                ghost.setState(Ghost.State.CHASE);
            }
        }
        powerUpRemainingTime = 0;
        powerUpTimer = null;
    }

    public void addScore(int points) {
        score += points;
        notifyScoreChanged(score);
    }

    public void loseLife() {
        lives--;
        notifyLivesChanged(lives);
        if (lives <= 0) {
            endGame(false);
        } else {
            resetAfterDeath();
        }
    }

    private void resetAfterDeath() {
        pacman.resetToStart();
        for (Ghost ghost : ghosts) {
            ghost.resetGhost();
        }
        startGameWithDelay();
    }

    public void checkWinCondition() {
        if (boardManager.getTotalDotsToCollect() <= 0) {
            endGame(true);
        }
    }

    public void endGame(boolean isWin) {
        currentState = isWin ? GameStateType.WIN : GameStateType.GAME_OVER;
        notifyGameStateChanged(currentState);
    }

    private void updateFloatingTexts() {
        floatingTexts.removeIf(ft -> !ft.isAlive());
    }

    public void addFloatingText(int x, int y, String text, long duration) {
        floatingTexts.add(new FloatingText(x, y, text, duration));
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    private void notifyScoreChanged(int newScore) {
        listeners.forEach(l -> l.onScoreChanged(newScore));
    }

    private void notifyLivesChanged(int livesRemaining) {
        listeners.forEach(l -> l.onLivesChanged(livesRemaining));
    }

    private void notifyGameStateChanged(GameStateType state) {
        listeners.forEach(l -> l.onGameStateChanged(state));
    }

    public void pauseGame() {
        if (currentState == GameStateType.PLAYING) {
            currentState = GameStateType.PAUSED;

            if (isStartDelay) {
                startDelayRemainingTime = startDelayEndTime - System.currentTimeMillis();
            }

            if (powerUpTimer != null) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - powerUpPauseTime;
                powerUpRemainingTime -= elapsedTime;

                powerUpTimer.cancel();
                powerUpTimer = null;
            }

            for (Ghost ghost : ghosts) {
                ghost.pauseFrightened();
            }
        }
    }

    public void resumeGame() {
        if (currentState == GameStateType.PAUSED) {
            currentState = GameStateType.PLAYING;

            if (startDelayRemainingTime > 0) {
                isStartDelay = true;
                startDelayEndTime = System.currentTimeMillis() + startDelayRemainingTime;
                startDelayRemainingTime = 0;
            }

            if (powerUpRemainingTime > 0) {
                powerUpPauseTime = System.currentTimeMillis();
                powerUpTimer = new Timer();
                powerUpTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        deactivatePowerUp();
                    }
                }, powerUpRemainingTime);
            }

            for (Ghost ghost : ghosts) {
                ghost.resumeFrightened();
            }
        }
    }

    public BoardManager getBoardManager() { return boardManager; }
    public ArrayList<Ghost> getGhosts() { return ghosts; }
    public ArrayList<FloatingText> getFloatingTexts() { return floatingTexts; }
    public PacMan getPacman() { return pacman; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getGhostMultiplier() { return ghostMultiplier; }
    public void incrementGhostMultiplier() { ghostMultiplier++; }
    public GameStateType getCurrentState() { return currentState; }
    public void setCurrentState(GameStateType state) { currentState = state; }
}
