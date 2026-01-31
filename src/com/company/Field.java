package com.company;

import java.util.ArrayList;

public class Field {
    enum FieldType { HORIZONTAL, VERTICAL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, EMPTY }

    private FieldType fieldType;
    private Boolean isWalkable;
    private Boolean hasDot;
    private Boolean hasBoost;
    private ArrayList<Direction> directions;
    public final int gridX;
    public final int gridY;
    public int gCost;
    public int hCost;
    public Field parent;
    private static int[][] boardValues;

    public int fCost() {
        return gCost + hCost;
    }

    public static void setBoardValues(int[][] values) {
        boardValues = values;
    }

    public Field(FieldType type, Boolean dot, Boolean boost, int x, int y) {
        fieldType = type;
        hasDot = dot;
        hasBoost = boost;
        isWalkable = fieldType == FieldType.EMPTY;
        if (boardValues != null && boardValues[x][y] == 0) {
            isWalkable = true;
        }
        directions = new ArrayList<>();
        gridX = x;
        gridY = y;
        setDirections(x, y);
    }

    private void setDirections(int xGrid, int yGrid) {
        if (boardValues == null) return;

        int val = boardValues[xGrid][yGrid];
        if (val == 5 || val == 6 || val == 7 || val == 0) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if ((x != 0 && y != 0) || (x == 0 && y == 0)) continue;
                    if (isValidField(xGrid + x, yGrid + y)) {
                        int neighborVal = boardValues[xGrid + x][yGrid + y];
                        if (neighborVal == 5 || neighborVal == 6 || neighborVal == 7 || neighborVal == 0) {
                            addDirection(x, y);
                        }
                    }
                }
            }
        }
    }

    private void addDirection(int x, int y) {
        if (x > 0) directions.add(Direction.RIGHT);
        else if (x < 0) directions.add(Direction.LEFT);
        else if (y > 0) directions.add(Direction.DOWN);
        else if (y < 0) directions.add(Direction.UP);
    }

    public static boolean isValidField(int x, int y) {
        return x >= 0 && y >= 0 && x < GameConstants.BOARD_WIDTH && y < GameConstants.BOARD_HEIGHT;
    }

    public int eatContent() {
        if (fieldType != FieldType.EMPTY) return 0;
        if (hasDot) {
            hasDot = false;
            return 1;
        }
        if (hasBoost) {
            hasBoost = false;
            return 2;
        }
        return 0;
    }

    public ArrayList<Direction> getDirections() { return directions; }
    public FieldType getFieldType() { return fieldType; }
    public Boolean getIsWalkable() { return isWalkable; }
    public Boolean getHasDot() { return hasDot; }
    public Boolean getHasBoost() { return hasBoost; }
}
