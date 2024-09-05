package com.kessoku.bocchifrog.particles;

import com.kessoku.bocchifrog.Vector2F;

public abstract class TimedParticle extends Particle {
    private final float initialTime;
    private float timeLeft;

    public TimedParticle(Vector2F position, float time) {
        super(position);
        initialTime = time;
        timeLeft = time;
    }

    @Override
    public void update(float deltaTime) {
        timeLeft -= deltaTime;
    }

    @Override
    public boolean shouldDeSpawn() {
        return timeLeft <= 0;
    }

    public float getInitialTime() {
        return initialTime;
    }

    public float getTimeLeft() {
        return timeLeft;
    }
}
