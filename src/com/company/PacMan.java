package com.company;

public class PacMan {
    private int xGrid;
    private int yGrid;
    private int xPixel;
    private int yPixel;
    private Direction currentDirection;
    private Direction nextDirection;
    private final int moveSpeed;
    private final int startXPixel;
    private final int startYPixel;
    private final Field[][] board;

    public PacMan(int startXPixel, int startYPixel, Field[][] board) {
        this.startXPixel = startXPixel;
        this.startYPixel = startYPixel;
        this.board = board;
        this.moveSpeed = 1;
        this.xPixel = startXPixel;
        this.yPixel = startYPixel;
        this.xGrid = startXPixel / GameConstants.TILE_SIZE;
        this.yGrid = startYPixel / GameConstants.TILE_SIZE;
        this.currentDirection = Direction.RIGHT;
        this.nextDirection = Direction.RIGHT;
    }

    private void changeDirection() {
        if (isHorizontalDirection(currentDirection)) {
            updateHorizontalDirection();
        } else {
            updateVerticalDirection();
        }
    }

    private boolean isHorizontalDirection(Direction dir) {
        return dir == Direction.RIGHT || dir == Direction.LEFT;
    }

    private void updateHorizontalDirection() {
        if (canChangeToHorizontal(Direction.RIGHT)) currentDirection = Direction.RIGHT;
        else if (canChangeToHorizontal(Direction.LEFT)) currentDirection = Direction.LEFT;
        else if (canChangeToVertical(Direction.DOWN)) currentDirection = Direction.DOWN;
        else if (canChangeToVertical(Direction.UP)) currentDirection = Direction.UP;
    }

    private void updateVerticalDirection() {
        if (canChangeToVertical(Direction.DOWN)) currentDirection = Direction.DOWN;
        else if (canChangeToVertical(Direction.UP)) currentDirection = Direction.UP;
        else if (canChangeToHorizontal(Direction.RIGHT)) currentDirection = Direction.RIGHT;
        else if (canChangeToHorizontal(Direction.LEFT)) currentDirection = Direction.LEFT;
    }

    private boolean canChangeToHorizontal(Direction dir) {
        return nextDirection == dir && board[xGrid][yGrid].getDirections().contains(dir);
    }

    private boolean canChangeToVertical(Direction dir) {
        return nextDirection == dir && board[xGrid][yGrid].getDirections().contains(dir) &&
               (isHorizontalDirection(currentDirection) ? isAlignedVertically() : true);
    }

    private boolean isAlignedVertically() {
        return xPixel % GameConstants.TILE_SIZE == 0;
    }

    private void moveToField(int x, int y) {
        int diffX = x * GameConstants.TILE_SIZE - xPixel;
        int diffY = y * GameConstants.TILE_SIZE - yPixel;

        if (diffX != 0) {
            xPixel += (diffX > 0) ? moveSpeed : -moveSpeed;
        } else if (diffY != 0) {
            yPixel += (diffY > 0) ? moveSpeed : -moveSpeed;
        }

        updateGridPosition();
    }

    private void updateGridPosition() {
        if (xPixel % GameConstants.TILE_SIZE == 0) xGrid = xPixel / GameConstants.TILE_SIZE;
        if (yPixel % GameConstants.TILE_SIZE == 0) yGrid = yPixel / GameConstants.TILE_SIZE;
    }

    public void move() {
        changeDirection();

        switch (currentDirection) {
            case RIGHT -> moveRight();
            case LEFT -> moveLeft();
            case UP -> moveUp();
            case DOWN -> moveDown();
        }

        handleFieldContent();
    }

    private void moveRight() {
        if (Field.isValidField(xGrid + 1, yGrid)) {
            if (board[xGrid + 1][yGrid].getIsWalkable()) moveToField(xGrid + 1, yGrid);
        } else {
            xGrid = 0;
            xPixel = 0;
        }
    }

    private void moveLeft() {
        if (Field.isValidField(xGrid - 1, yGrid)) {
            if (board[xGrid - 1][yGrid].getIsWalkable()) moveToField(xGrid - 1, yGrid);
        } else {
            xGrid = GameConstants.BOARD_WIDTH - 1;
            xPixel = (GameConstants.BOARD_WIDTH - 1) * GameConstants.TILE_SIZE;
        }
    }

    private void moveUp() {
        if (board[xGrid][yGrid - 1].getIsWalkable()) moveToField(xGrid, yGrid - 1);
    }

    private void moveDown() {
        if (board[xGrid][yGrid + 1].getIsWalkable()) moveToField(xGrid, yGrid + 1);
    }

    private void handleFieldContent() {
    }

    public void resetToStart() {
        xPixel = startXPixel;
        yPixel = startYPixel;
        xGrid = startXPixel / GameConstants.TILE_SIZE;
        yGrid = startYPixel / GameConstants.TILE_SIZE;
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
    }

    public int getXGrid() { return xGrid; }
    public int getYGrid() { return yGrid; }
    public int getXPixel() { return xPixel; }
    public int getYPixel() { return yPixel; }
    public void setNextDirection(Direction direction) { nextDirection = direction; }
    public Direction getCurrentDirection() { return currentDirection; }
}