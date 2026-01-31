package com.company;

public class CollisionHandlerImpl implements CollisionHandler {

    @Override

    public void checkCollisions(GameEngine engine) {
        PacMan pacman = engine.getPacman();
        java.util.ArrayList<Ghost> ghosts = engine.getGhosts();

        for (Ghost ghost : ghosts) {
            double dist = calculateDistance(
                pacman.getXPixel(), pacman.getYPixel(),
                ghost.getXPixel(), ghost.getYPixel()
            );

            if (dist < 12) {
                if (ghost.getState() == Ghost.State.FRIGHTENED) {
                    ghost.setState(Ghost.State.EATEN);
                    int pointsEarned = 200 * engine.getGhostMultiplier();
                    engine.addScore(pointsEarned);
                    engine.addFloatingText(ghost.getXPixel(), ghost.getYPixel(), "+" + pointsEarned, 2000);
                    engine.incrementGhostMultiplier();
                } else if (ghost.getState() == Ghost.State.CHASE || ghost.getState() == Ghost.State.SCUTTER) {
                    engine.loseLife();
                }
            }
        }
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        double a = ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1));
        a = Math.abs(a);
        return Math.sqrt(a);
    }
}
