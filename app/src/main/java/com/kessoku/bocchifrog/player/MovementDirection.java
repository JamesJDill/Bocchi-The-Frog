package com.kessoku.bocchifrog.player;

import com.kessoku.bocchifrog.Vector2F;

public enum MovementDirection {
    UP, DOWN, LEFT, RIGHT;

    Vector2F unitVector() {
        switch (this) {
        case UP:
            return new Vector2F(0, -1);
        case DOWN:
            return new Vector2F(0, +1);
        case LEFT:
            return new Vector2F(-1, 0);
        case RIGHT:
            return new Vector2F(+1, 0);

        default:
            throw new RuntimeException("Unrecognized MovementDirection!");
        }
    }

    Vector2F calcVelocityForJump(float totalJumpTime) {
        if (totalJumpTime == 0) {
            throw new IllegalArgumentException("totalJumpTime can not be zero!");
        }

        float velocityMag = 1 / totalJumpTime;

        return unitVector().scaledBy(velocityMag);
    }
}
