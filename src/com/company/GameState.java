package com.company;

public interface GameState {
    void update();
    void render(java.awt.Graphics g);
    void enter();
    void exit();
}
