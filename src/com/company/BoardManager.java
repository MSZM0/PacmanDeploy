package com.company;

public class BoardManager {
    private final int[][] boardValues;
    private final Field[][] board;
    private int totalDotsToCollect;

    public BoardManager(int width, int height) {
        this.boardValues = new int[width][height];
        this.board = new Field[width][height];
        this.totalDotsToCollect = 0;
    }

    public void loadFromFile(String filename) throws java.io.FileNotFoundException {
        java.io.File file = new java.io.File(filename);
        java.util.Scanner scanner = new java.util.Scanner(file);

        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                if (scanner.hasNext()) {
                    boardValues[x][y] = scanner.nextInt();
                }
            }
        }
        scanner.close();
    }

    public void buildBoard() {
        Field.setBoardValues(boardValues);
        totalDotsToCollect = 0;
        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                board[x][y] = createField(boardValues[x][y], x, y);
                if (board[x][y].getHasDot() || board[x][y].getHasBoost()) {
                    totalDotsToCollect++;
                }
            }
        }
    }

    private Field createField(int value, int x, int y) {
        return switch (value) {
            case 1 -> new Field(Field.FieldType.BOTTOM_RIGHT, false, false, x, y);
            case 2 -> new Field(Field.FieldType.BOTTOM_LEFT, false, false, x, y);
            case 3 -> new Field(Field.FieldType.TOP_RIGHT, false, false, x, y);
            case 4 -> new Field(Field.FieldType.TOP_LEFT, false, false, x, y);
            case 5 -> new Field(Field.FieldType.EMPTY, true, false, x, y);
            case 6, 0 -> new Field(Field.FieldType.EMPTY, false, false, x, y);
            case 7 -> new Field(Field.FieldType.EMPTY, false, true, x, y);
            case 8 -> new Field(Field.FieldType.VERTICAL, false, false, x, y);
            case 9 -> new Field(Field.FieldType.HORIZONTAL, false, false, x, y);
            default -> new Field(Field.FieldType.EMPTY, false, false, x, y);
        };
    }

    public Field[][] getBoard() {
        return board;
    }

    public int getTotalDotsToCollect() {
        return totalDotsToCollect;
    }

    public void decrementDotCount() {
        totalDotsToCollect--;
    }
}
