package com.company;

public class FloatingText {
    public int x;
    public int y;
    public String text;
    public long createdTime;
    public long duration;

    public FloatingText(int x, int y, String text, long duration) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.duration = duration;
        this.createdTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - createdTime) < duration;
    }

    public float getAlpha() {
        long elapsed = System.currentTimeMillis() - createdTime;
        if (elapsed > duration - 500) {
            return 1.0f - ((elapsed - (duration - 500)) / 500.0f);
        }
        return 1.0f;
    }
}
