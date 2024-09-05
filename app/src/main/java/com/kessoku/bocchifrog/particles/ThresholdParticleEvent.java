package com.kessoku.bocchifrog.particles;

import java.util.function.Consumer;

public class ThresholdParticleEvent {
    private final float thresholdTime;
    private final Consumer<ParticleSystem> event;

    private boolean activated = false;

    public ThresholdParticleEvent(float thresholdTime, Consumer<ParticleSystem> event) {
        this.thresholdTime = thresholdTime;
        this.event = event;
    }

    public void tryActivate(float timeLeft, ParticleSystem particleSystem) {
        if (!activated && timeLeft <= thresholdTime) {
            event.accept(particleSystem);
            activated = true;
        }
    }
}
