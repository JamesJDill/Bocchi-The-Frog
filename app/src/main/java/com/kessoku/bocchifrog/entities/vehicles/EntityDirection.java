package com.kessoku.bocchifrog.entities.vehicles;

public enum EntityDirection {
    LEFT, RIGHT;

    public int movementSign() {
        return (this == LEFT) ? -1 : +1;
    }
}
