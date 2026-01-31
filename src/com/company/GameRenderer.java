package com.company;

import java.awt.*;
import javax.swing.ImageIcon;

public class GameRenderer {
    private final TextureManager textureManager;
    private int ghostAnimationFrame = 0;
    private int pacmanAnimationFrame = 0;

    public GameRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void render(Graphics g, GameEngine engine) {
        Field[][] board = engine.getBoardManager().getBoard();

        drawMap(g, board);
        drawGhosts(g, engine);
        drawPacman(g, engine.getPacman());
        drawFloatingTexts(g, engine);
    }

    private void drawMap(Graphics g, Field[][] board) {
        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                drawField(g, board[x][y], x, y);
            }
        }
    }

    private void drawField(Graphics g, Field field, int x, int y) {
        int drawX = x * GameConstants.TILE_SIZE * GameConstants.SCALE;
        int drawY = y * GameConstants.TILE_SIZE * GameConstants.SCALE;
        int size = GameConstants.SCALE * GameConstants.TILE_SIZE;

        switch (field.getFieldType()) {
            case VERTICAL -> g.drawImage(textureManager.getVertical().getImage(), drawX, drawY, size, size, null);
            case HORIZONTAL -> g.drawImage(textureManager.getHorizontal().getImage(), drawX, drawY, size, size, null);
            case BOTTOM_LEFT -> g.drawImage(textureManager.getBottomLeft().getImage(), drawX, drawY, size, size, null);
            case BOTTOM_RIGHT -> g.drawImage(textureManager.getBottomRight().getImage(), drawX, drawY, size, size, null);
            case TOP_LEFT -> g.drawImage(textureManager.getTopLeft().getImage(), drawX, drawY, size, size, null);
            case TOP_RIGHT -> g.drawImage(textureManager.getTopRight().getImage(), drawX, drawY, size, size, null);
            case EMPTY -> drawEmptyField(g, field, drawX, drawY, size);
        }
    }

    private void drawEmptyField(Graphics g, Field field, int x, int y, int size) {
        if (field.getHasBoost()) {
            g.drawImage(textureManager.getBoost().getImage(), x, y, size, size, null);
        } else if (field.getHasDot()) {
            g.drawImage(textureManager.getDot().getImage(), x, y, size, size, null);
        } else {
            g.drawImage(textureManager.getEmpty().getImage(), x, y, size, size, null);
        }
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GameConstants.BOARD_WIDTH; i++) {
            g.drawLine(i * GameConstants.SCALE * GameConstants.TILE_SIZE, 0,
                    i * GameConstants.SCALE * GameConstants.TILE_SIZE,
                    GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE);
        }
        for (int i = 0; i < GameConstants.BOARD_HEIGHT; i++) {
            g.drawLine(0, i * GameConstants.SCALE * GameConstants.TILE_SIZE,
                    GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
                    i * GameConstants.TILE_SIZE * GameConstants.SCALE);
        }
    }

    private void drawGhosts(Graphics g, GameEngine engine) {
        ghostAnimationFrame++;
        if (ghostAnimationFrame > 6) ghostAnimationFrame = 0;

        int n = ghostAnimationFrame > 3 ? 1 : 0;

        for (Ghost ghost : engine.getGhosts()) {
            if (ghost.getState() == Ghost.State.EATEN) continue;

            ImageIcon ghostTexture = getGhostTexture(ghost);
            int drawX = ghost.getXPixel() * GameConstants.SCALE;
            int drawY = ghost.getYPixel() * GameConstants.SCALE;
            int endX = (ghost.getXPixel() + GameConstants.TILE_SIZE) * GameConstants.SCALE;
            int endY = (ghost.getYPixel() + GameConstants.TILE_SIZE) * GameConstants.SCALE;

            g.drawImage(ghostTexture.getImage(), drawX, drawY, endX, endY,
                    GameConstants.TILE_SIZE * n, 0, GameConstants.TILE_SIZE * (n + 1), GameConstants.TILE_SIZE, null);
        }

        drawGhostEyes(g, engine.getGhosts());
    }

    private ImageIcon getGhostTexture(Ghost ghost) {
        if (ghost.getState() == Ghost.State.FRIGHTENED) {
            long timeSinceFrightStart = System.currentTimeMillis() - ghost.getFrightenedStartTime();
            long remainingTime = GameConstants.FRIGHT_DURATION - timeSinceFrightStart;

            if (remainingTime <= GameConstants.FRIGHT_BLINK_START && remainingTime > 0) {
                return ((System.currentTimeMillis() / 150) % 2 == 0) ?
                        textureManager.getFrightenedGhost() :
                        textureManager.getEndFrightGhost();
            }
            return textureManager.getFrightenedGhost();
        }

        return switch (ghost.getGhostType()) {
            case RED -> textureManager.getRedGhost();
            case ORANGE -> textureManager.getOrangeGhost();
            case BLUE -> textureManager.getBlueGhost();
            case PINK -> textureManager.getPinkGhost();
        };
    }

    private void drawGhostEyes(Graphics g, java.util.ArrayList<Ghost> ghosts) {
        for (Ghost ghost : ghosts) {
            if (ghost.getState() == Ghost.State.EATEN || ghost.getState() == Ghost.State.FRIGHTENED) continue;

            ImageIcon eyesTexture = switch (ghost.getDirection()) {
                case RIGHT -> textureManager.getEyesRight();
                case LEFT -> textureManager.getEyesLeft();
                case UP -> textureManager.getEyesUp();
                case DOWN -> textureManager.getEyesDown();
            };

            g.drawImage(eyesTexture.getImage(),
                    ghost.getXPixel() * GameConstants.SCALE,
                    ghost.getYPixel() * GameConstants.SCALE,
                    GameConstants.SCALE * GameConstants.TILE_SIZE,
                    GameConstants.SCALE * GameConstants.TILE_SIZE, null);
        }
    }

    private void drawPacman(Graphics g, PacMan pacman) {
        pacmanAnimationFrame++;
        if (pacmanAnimationFrame > 8) pacmanAnimationFrame = 0;

        int n = 0;
        if (pacmanAnimationFrame > 2) n = 1;
        if (pacmanAnimationFrame > 4) n = 2;
        if (pacmanAnimationFrame > 6) n = 3;

        int direction = switch (pacman.getCurrentDirection()) {
            case RIGHT -> 0;
            case LEFT -> 1;
            case DOWN -> 2;
            case UP -> 3;
        };

        int drawX = pacman.getXPixel() * GameConstants.SCALE;
        int drawY = pacman.getYPixel() * GameConstants.SCALE;
        int endX = (pacman.getXPixel() + GameConstants.TILE_SIZE) * GameConstants.SCALE;
        int endY = (pacman.getYPixel() + GameConstants.TILE_SIZE) * GameConstants.SCALE;

        g.drawImage(textureManager.getPacmanFull().getImage(), drawX, drawY, endX, endY,
                GameConstants.TILE_SIZE * n, GameConstants.TILE_SIZE * direction,
                GameConstants.TILE_SIZE * (n + 1), GameConstants.TILE_SIZE * (direction + 1), null);
    }

    private void drawFloatingTexts(Graphics g, GameEngine engine) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 24));

        synchronized (engine.getFloatingTexts()) {
            for (FloatingText ft : engine.getFloatingTexts()) {
                int alpha = Math.max(0, (int) (ft.getAlpha() * 255));
                Color color = new Color(255, 255, 0, alpha);
                g2d.setColor(color);
                g2d.drawString(ft.text, ft.x * GameConstants.SCALE, (ft.y - 5) * GameConstants.SCALE);
            }
        }
    }
}
