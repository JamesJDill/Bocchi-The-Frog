package com.kessoku.bocchifrog.terrain;

import com.kessoku.bocchifrog.R;

public enum ObstacleType {
    ROCK, TREE;
    public int getSpriteId() {
        switch (this) {
        case ROCK:
            return R.drawable.obstacle_rock;
        case TREE:
            return R.drawable.kita_upscale_up;
        default:
            throw new RuntimeException("Unrecognized ObstacleType!");
        }
    }
}
