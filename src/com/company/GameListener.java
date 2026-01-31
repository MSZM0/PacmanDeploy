package com.company;

public interface GameListener {
    void onGameStateChanged(GameStateType state);
    void onScoreChanged(int newScore);
    void onLivesChanged(int livesRemaining);
}
