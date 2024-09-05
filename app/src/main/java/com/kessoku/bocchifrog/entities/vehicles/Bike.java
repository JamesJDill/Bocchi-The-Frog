package com.kessoku.bocchifrog.entities.vehicles;

import com.kessoku.bocchifrog.R;

public class Bike extends Vehicle {
    @Override
    protected int findSpriteId() {
        return facingRight() ? R.drawable.bike_right : R.drawable.bike_left;
    }

    @Override
    protected float getSpeed() {
        return 2.0f;
    }

    @Override
    protected float getHeight() {
        return 0.5f;
    }

    @Override
    protected float getLength() {
        return 14.0f / 16.0f;
    }

    @Override
    public float getSpawningPeriod() {
        return 2.0f;
    }

    @Override
    public int getPointBonus() {
        return 1;
    }
}
