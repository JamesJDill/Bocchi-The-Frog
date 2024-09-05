package com.kessoku.bocchifrog.entities.vehicles;

import com.kessoku.bocchifrog.R;

public class Truck extends Vehicle {
    @Override
    protected int findSpriteId() {
        return facingRight() ? R.drawable.truck_right : R.drawable.truck_left;
    }

    @Override
    protected float getSpeed() {
        return 3.0f;
    }

    @Override
    protected float getLength() {
        return 38.0f / 16.0f;
    }

    @Override
    public float getShadowExpansionPadding() {
        return 0.15f;
    }

    @Override
    public float getSpawningPeriod() {
        return 3.0f;
    }

    @Override
    public int getPointBonus() {
        return 3;
    }
}
