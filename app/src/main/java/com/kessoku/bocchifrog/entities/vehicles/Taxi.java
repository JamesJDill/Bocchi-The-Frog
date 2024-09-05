package com.kessoku.bocchifrog.entities.vehicles;

import com.kessoku.bocchifrog.R;

public class Taxi extends Vehicle {
    @Override
    protected int findSpriteId() {
        return facingRight() ? R.drawable.taxi_right : R.drawable.taxi_left;
    }

    @Override
    protected float getSpeed() {
        return 4.0f;
    }

    @Override
    protected float getLength() {
        return 1.5f;
    }

    @Override
    public float getShadowExpansionPadding() {
        return 0.15f;
    }

    @Override
    public float getSpawningPeriod() {
        return 1.5f;
    }

    @Override
    public int getPointBonus() {
        return 2;
    }

}
