package com.kessoku.bocchifrog.particles;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Vector2F;

public abstract class Particle {
    private final Vector2F position;

    protected Particle(Vector2F position) {
        this.position = position;
    }

    public abstract void update(float deltaTime);

    public abstract void draw(@NonNull Canvas canvas);

    public abstract boolean shouldDeSpawn();

    public Vector2F getPosition() {
        return position;
    }
}
