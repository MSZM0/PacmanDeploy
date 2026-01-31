package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Pathfinding {
    enum GhostType { RED, PINK, BLUE, ORANGE }

    private static Field startField;
    private static Field endField;
    private static ArrayList<Field> openSet;
    private static HashSet<Field> closedSet;
    private static GhostType currentGhostType;
    private static Field[][] board;



    public static ArrayList<Field> findPath(int startX, int startY, int targetX, int targetY, Direction direction, GhostType ghostType, Field[][] boardRef) {
        board = boardRef;
        startField = board[startX][startY];
        endField = board[targetX][targetY];
        currentGhostType = ghostType;

        openSet = new ArrayList<>();
        closedSet = new HashSet<>();
        openSet.add(startField);

        while (!openSet.isEmpty()) {
            Field currentField = openSet.get(0);
            for (int i = 1; i < openSet.size(); i++) {
                Field candidate = openSet.get(i);
                if (shouldSwapField(candidate, currentField)) {
                    currentField = candidate;
                }
            }
            openSet.remove(currentField);
            closedSet.add(currentField);

            if (currentField == endField) {
                return reconstructPath();
            }

            for (Field neighbor : getNeighbors(currentField)) {
                if (neighborOppositeToDirection(neighbor, direction, startX, startY)) continue;
                if (!neighbor.getIsWalkable() || closedSet.contains(neighbor)) continue;

                int newMovementCostToNeighbor = currentField.gCost + getDistance(currentField, neighbor, ghostType);
                if (newMovementCostToNeighbor < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.gCost = newMovementCostToNeighbor;
                    neighbor.hCost = getDistance(neighbor, endField, ghostType);
                    neighbor.parent = currentField;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private static boolean shouldSwapField(Field candidate, Field currentField) {
        return switch (currentGhostType) {
            case RED -> candidate.fCost() < currentField.fCost() ||
                      (candidate.fCost() == currentField.fCost() && candidate.hCost < currentField.hCost);
            case PINK -> candidate.hCost < currentField.hCost ||
                       (candidate.hCost == currentField.hCost && candidate.fCost() < currentField.fCost());
            case BLUE -> candidate.fCost() < currentField.fCost() ||
                       (Math.abs(candidate.fCost() - currentField.fCost()) < 3 && candidate.hCost < currentField.hCost);
            case ORANGE -> candidate.fCost() < currentField.fCost() * 1.05 ||
                          (candidate.fCost() < currentField.fCost() && candidate.hCost < currentField.hCost);
        };
    }

    private static ArrayList<Field> reconstructPath() {
        ArrayList<Field> path = new ArrayList<>();
        Field currentField = endField;
        while (currentField != startField) {
            path.add(currentField);
            currentField = currentField.parent;
        }
        Collections.reverse(path);
        return path;
    }


    private static int getDistance(Field fieldA, Field fieldB, GhostType ghostType) {
        int dstX = Math.abs(fieldA.gridX - fieldB.gridX);
        int dstY = Math.abs(fieldA.gridY - fieldB.gridY);
        return 10 * (dstX + dstY);
    }

    private static ArrayList<Field> getNeighbors(Field field) {
        ArrayList<Field> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || (x != 0 && y != 0)) continue;
                int checkX = field.gridX + x;
                int checkY = field.gridY + y;
                if (Field.isValidField(checkX, checkY)) {
                    neighbors.add(board[checkX][checkY]);
                }
            }
        }
        return neighbors;
    }

    private static Boolean neighborOppositeToDirection(Field neighbor, Direction direction, int startX, int startY) {
        return switch (direction) {
            case RIGHT -> neighbor.gridX == startX - 1 && neighbor.gridY == startY;
            case LEFT -> neighbor.gridX == startX + 1 && neighbor.gridY == startY;
            case DOWN -> neighbor.gridX == startX && neighbor.gridY == startY - 1;
            case UP -> neighbor.gridX == startX && neighbor.gridY == startY + 1;
        };
    }
}
