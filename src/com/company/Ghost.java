package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Ghost {
    enum GhostType { BLUE, PINK, ORANGE, RED }
    enum State { CHASE, FRIGHTENED, EATEN, SCUTTER }

    static ArrayList<Ghost> ghostList;
    static PacMan pacman;
    static final int TILE_SIZE = GameConstants.TILE_SIZE;
    static final int BOARD_WIDTH = GameConstants.BOARD_WIDTH;
    static final int BOARD_HEIGHT = GameConstants.BOARD_HEIGHT;
    static final long SCUTTER_DURATION = 5000;
    static final long RESPAWN_DELAY = 1000;

    private final GhostType ghostType;
    private final Field[][] board;
    private int xGrid;
    private int yGrid;
    private int xPixel;
    private int yPixel;
    private final int startX;
    private final int startY;
    private int targetX;
    private int targetY;
    private Direction direction;
    private State state;
    private double speed;
    private double movementAccumulator;
    private ArrayList<Field> path;
    private long scutterStartTime;
    private long frightenedStartTime;
    private long eatenTime;
    private long frightenedPausedTime;

    public Ghost(GhostType type, Field[][] board) {
        this.ghostType = type;
        this.board = board;
        this.speed = 1.0;
        this.movementAccumulator = 0.0;
        this.state = State.CHASE;
        this.path = new ArrayList<>();

        int[] startPos = getStartPosition(type);
        this.startX = startPos[0];
        this.startY = startPos[1];

        resetGhost();
    }

    private int[] getStartPosition(GhostType type) {
        return switch (type) {
            case RED -> new int[]{13 * GameConstants.TILE_SIZE, 11 * GameConstants.TILE_SIZE};
            case PINK -> new int[]{14 * GameConstants.TILE_SIZE, 11 * GameConstants.TILE_SIZE};
            case BLUE -> new int[]{13 * GameConstants.TILE_SIZE, 14 * GameConstants.TILE_SIZE};
            case ORANGE -> new int[]{14 * GameConstants.TILE_SIZE, 14 * GameConstants.TILE_SIZE};
        };
    }

    public void resetGhost() {
        this.xPixel = startX;
        this.yPixel = startY;
        this.xGrid = xPixel / GameConstants.TILE_SIZE;
        this.yGrid = yPixel / GameConstants.TILE_SIZE;
        this.direction = Direction.UP;
        this.movementAccumulator = 0.0;
        this.path.clear();
        this.state = State.SCUTTER;
        this.scutterStartTime = System.currentTimeMillis();
    }

    private void moveToField(int x, int y) {
        int diffX = x * GameConstants.TILE_SIZE - xPixel;
        int diffY = y * GameConstants.TILE_SIZE - yPixel;

        movementAccumulator += speed;
        int pixelMove = (int) movementAccumulator;
        movementAccumulator -= pixelMove;

        if (diffX != 0) {
            xPixel += (diffX > 0) ? pixelMove : -pixelMove;
        } else if (diffY != 0) {
            yPixel += (diffY > 0) ? pixelMove : -pixelMove;
        }

        updateGridPosition();
        updateDirection(diffX, diffY);
    }

    private void updateGridPosition() {
        if (xPixel % GameConstants.TILE_SIZE == 0) xGrid = xPixel / GameConstants.TILE_SIZE;
        if (yPixel % GameConstants.TILE_SIZE == 0) yGrid = yPixel / GameConstants.TILE_SIZE;
    }

    private void updateDirection(int diffX, int diffY) {
        if (diffX > 0 && diffY == 0) direction = Direction.RIGHT;
        else if (diffX < 0 && diffY == 0) direction = Direction.LEFT;
        else if (diffX == 0 && diffY < 0) direction = Direction.UP;
        else if (diffX == 0 && diffY > 0) direction = Direction.DOWN;
    }

    private void moveToTarget(ArrayList<Field> path) {
        if (path.isEmpty()) {
            addRandomDirection();
        }
        if (!path.isEmpty() && path.get(0).gridX * GameConstants.TILE_SIZE == xPixel &&
            path.get(0).gridY * GameConstants.TILE_SIZE == yPixel) {
            path.remove(0);
        }
        if (!path.isEmpty()) {
            moveToField(path.get(0).gridX, path.get(0).gridY);
        }
    }

    private void addRandomDirection() {
        ArrayList<Direction> availableDirections = board[xGrid][yGrid].getDirections();
        if (!availableDirections.isEmpty()) {
            int rng = new Random().nextInt(availableDirections.size());
            Direction dir = availableDirections.get(rng);
            switch (dir) {
                case RIGHT -> path.add(board[xGrid + 1][yGrid]);
                case LEFT -> path.add(board[xGrid - 1][yGrid]);
                case DOWN -> path.add(board[xGrid][yGrid + 1]);
                case UP -> path.add(board[xGrid][yGrid - 1]);
            }
        }
    }

    public static void moveGhosts(ArrayList<Ghost> ghosts, PacMan pacmanRef, Field[][] boardRef){
        ghostList = ghosts;
        pacman = pacmanRef;
        for (Ghost ghost : ghostList) {

            if (ghost.state == State.EATEN) {
                if (ghost.eatenTime == 0) {
                    ghost.eatenTime = System.currentTimeMillis();
                }
                long elapsedSinceEaten = System.currentTimeMillis() - ghost.eatenTime;
                if (elapsedSinceEaten < RESPAWN_DELAY) {
                    continue;
                } else {
                    ghost.resetGhost();
                    ghost.eatenTime = 0;
                    continue;
                }
            }

            if (ghost.state == State.EATEN) continue;

            if (ghost.state == State.SCUTTER) {
                long elapsed = System.currentTimeMillis() - ghost.scutterStartTime;
                if (elapsed > SCUTTER_DURATION) {
                    ghost.state = State.CHASE;
                }
            }

            if (ghost.getXPixel() % TILE_SIZE == 0 && ghost.getYPixel() % TILE_SIZE == 0) {

                if (ghost.state == State.FRIGHTENED) {
                    Random rand = new Random();
                    ghost.targetX = rand.nextInt(BOARD_WIDTH);
                    ghost.targetY = rand.nextInt(BOARD_HEIGHT);
                } else if (ghost.state == State.SCUTTER) {
                    switch (ghost.ghostType) {
                        case RED -> {
                            ghost.targetX = BOARD_WIDTH - 2;
                            ghost.targetY = 1;
                        }
                        case PINK -> {
                            ghost.targetX = 1;
                            ghost.targetY = 1;
                        }
                        case BLUE -> {
                            ghost.targetX = BOARD_WIDTH - 2;
                            ghost.targetY = BOARD_HEIGHT - 2;
                        }
                        case ORANGE -> {
                            ghost.targetX = 1;
                            ghost.targetY = BOARD_HEIGHT - 2;
                        }
                    }
                } else {
                    switch (ghost.ghostType) {
                        case RED -> {
                            ghost.targetX = pacman.getXGrid();
                            ghost.targetY = pacman.getYGrid();
                        }
                        case PINK -> {
                            int targetX = pacman.getXGrid();
                            int targetY = pacman.getYGrid();
                            switch (pacman.getCurrentDirection()) {
                                case RIGHT -> targetX += 2;
                                case LEFT -> targetX -= 2;
                                case DOWN -> targetY += 2;
                                case UP -> targetY -= 2;
                            }
                            if (Field.isValidField(targetX, targetY)) {
                                ghost.targetX = targetX;
                                ghost.targetY = targetY;
                            } else {
                                ghost.targetX = pacman.getXGrid();
                                ghost.targetY = pacman.getYGrid();
                            }
                        }
                        case BLUE -> {
                            int anchorX = pacman.getXGrid();
                            int anchorY = pacman.getYGrid();

                            switch (pacman.getCurrentDirection()) {
                                case RIGHT -> anchorX += 2;
                                case LEFT -> anchorX -= 2;
                                case DOWN -> anchorY += 2;
                                case UP -> anchorY -= 2;
                            }

                            if (Field.isValidField(anchorX, anchorY)) {
                                Ghost blinky = ghostList.get(0);
                                int offsetX = anchorX - blinky.getXGrid();
                                int offsetY = anchorY - blinky.getYGrid();

                                int tX = anchorX + offsetX;
                                int tY = anchorY + offsetY;

                                if (Field.isValidField(tX, tY)) {
                                    ghost.targetX = tX;
                                    ghost.targetY = tY;
                                } else {
                                    ghost.targetX = anchorX;
                                    ghost.targetY = anchorY;
                                }
                            } else {
                                ghost.targetX = pacman.getXGrid();
                                ghost.targetY = pacman.getYGrid();
                            }
                        }
                        case ORANGE -> {
                            double distance = getDistance(pacman.getXGrid(), pacman.getYGrid(), ghost.getXGrid(), ghost.getYGrid());
                            if (distance >= 8) {
                                ghost.targetX = pacman.getXGrid();
                                ghost.targetY = pacman.getYGrid();
                            } else {
                                ghost.targetX = 1;
                                ghost.targetY = BOARD_HEIGHT - 2;
                            }
                        }
                    }
                }
                ghost.path = Pathfinding.findPath(ghost.getXGrid(), ghost.getYGrid(), ghost.getTargetX(), ghost.getTargetY(), ghost.direction, Pathfinding.GhostType.valueOf(ghost.ghostType.toString()), ghost.board);
            }
            ghost.moveToTarget(ghost.path);
        }
    }

    private static double getDistance(int x1, int y1, int x2, int y2) {
        int a = Math.abs(x1 - x2);
        int b = Math.abs(y1 - y2);
        return a + b;
        //return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public GhostType getGhostType() { return ghostType; }
    public int getXPixel() { return xPixel; }
    public int getYPixel() { return yPixel; }
    public int getXGrid() { return xGrid; }
    public int getYGrid() { return yGrid; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public Direction getDirection() { return direction; }
    public State getState() { return state; }
    public long getFrightenedStartTime() { return frightenedStartTime; }

    public void setState(State state) {
        this.state = state;
        if (state == State.FRIGHTENED) {
            speed = 0.5;
            frightenedStartTime = System.currentTimeMillis();
        } else if (state == State.CHASE || state == State.SCUTTER || state == State.EATEN) {
            speed = 1.0;
        }
    }

    public void pauseFrightened() {
        if (state == State.FRIGHTENED && frightenedStartTime > 0) {
            frightenedPausedTime = System.currentTimeMillis() - frightenedStartTime;
        }
    }

    public void resumeFrightened() {
        if (state == State.FRIGHTENED && frightenedPausedTime > 0) {
            frightenedStartTime = System.currentTimeMillis() - frightenedPausedTime;
            frightenedPausedTime = 0;
        }
    }
}
