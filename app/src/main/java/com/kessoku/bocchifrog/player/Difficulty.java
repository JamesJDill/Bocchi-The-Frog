package com.kessoku.bocchifrog.player;

public enum Difficulty {
    NULL, EASY, NORMAL, HARD;

    public int getStartingHearts() {
        switch (this) {
        case EASY:
            return 5;
        case NORMAL:
            return 3;
        case HARD:
            return 1;
        default:
            throw new RuntimeException("Unrecognized Difficulty!");
        }
    }
}
