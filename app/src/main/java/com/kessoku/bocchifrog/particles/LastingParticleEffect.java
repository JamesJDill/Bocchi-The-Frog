package com.kessoku.bocchifrog.particles;

import androidx.annotation.NonNull;

public abstract class LastingParticleEffect extends ParticleEffect {

    private float timeLeft;

    public LastingParticleEffect(float timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public void begin(@NonNull ParticleSystem particleSystem) {
        particleSystem.beginLastingParticleEffect(this);
    }

    public void update(float deltaTime, ParticleSystem particleSystem) {
        timeLeft -= deltaTime;
    }

    protected float getTimeLeft() {
        return timeLeft;
    }

    public boolean shouldDelete() {
        return timeLeft <= 0;
    }
}
