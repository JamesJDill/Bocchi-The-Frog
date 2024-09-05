package com.kessoku.bocchifrog.entities.floaters;

import com.kessoku.bocchifrog.R;

public class Log extends Floater {
    @Override
    protected int findSpriteId() {
        return R.drawable.log;
    }

    @Override
    protected float getSpeed() {
        return 4.0f;
    }

    @Override
    protected float getLength() {
        return 5;
    }

    @Override
    public float getSpawningPeriod() {
        return 2.25f;
    }
}
