package com.kessoku.bocchifrog.player;

import com.kessoku.bocchifrog.R;

public enum PlayerCharacter {
    NULL, BOCCHI, KITA, NIJIKA, RYO;

    public int getSpriteId(MovementDirection direction) {
        switch (direction) {
        case UP:
            return getSpriteIdUp();
        case RIGHT:
            return getSpriteIdRight();
        case DOWN:
            return getSpriteIdDown();
        case LEFT:
            return getSpriteIdLeft();
        default:
            throw new RuntimeException("Unrecognized MovementDirection!");
        }
    }

    private int getSpriteIdUp() {
        switch (this) {
        case KITA:
            return R.drawable.kita_upscale_up;
        case NIJIKA:
            return R.drawable.nijika_upscale_up;
        case RYO:
            return R.drawable.ryo_upscale_up;
        case BOCCHI:
            return R.drawable.bocchi_upscale_up;
        default:
            throw new RuntimeException("Unrecognized PlayerCharacter!");
        }
    }

    private int getSpriteIdRight() {
        switch (this) {
        case KITA:
            return R.drawable.kita_upscale_right;
        case NIJIKA:
            return R.drawable.nijika_upscale_right;
        case RYO:
            return R.drawable.ryo_upscale_right;
        case BOCCHI:
            return R.drawable.bocchi_upscale_right;
        default:
            throw new RuntimeException("Unrecognized PlayerCharacter!");
        }
    }

    private int getSpriteIdDown() {
        switch (this) {
        case KITA:
            return R.drawable.kita_upscale_down;
        case NIJIKA:
            return R.drawable.nijika_upscale_down;
        case RYO:
            return R.drawable.ryo_upscale_down;
        case BOCCHI:
            return R.drawable.bocchi_upscale_down;
        default:
            throw new RuntimeException("Unrecognized PlayerCharacter!");
        }
    }
    private int getSpriteIdLeft() {
        switch (this) {
        case KITA:
            return R.drawable.kita_upscale_left;
        case NIJIKA:
            return R.drawable.nijika_upscale_left;
        case RYO:
            return R.drawable.ryo_upscale_left;
        case BOCCHI:
            return R.drawable.bocchi_upscale_left;
        default:
            throw new RuntimeException("Unrecognized PlayerCharacter!");
        }
    }
}
