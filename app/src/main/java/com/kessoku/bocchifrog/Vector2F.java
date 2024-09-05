package com.kessoku.bocchifrog;

import androidx.annotation.NonNull;

public class Vector2F {
    private float x;
    private float y;

    public Vector2F(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public Vector2F scaledBy(float scalar) {
        return new Vector2F(x * scalar, y * scalar);
    }

    public Vector2F displacedBy(Vector2F diff) {
        return new Vector2F(x + diff.getX(), y + diff.getY());
    }

    public void scale(float scalar) {
        x *= scalar;
        y *= scalar;
    }

    public void displace(float diffX, float diffY) {
        x += diffX;
        y += diffY;
    }

    public void displace(@NonNull Vector2F diff) {
        displace(diff.getX(), diff.getY());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
