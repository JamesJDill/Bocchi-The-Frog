package com.kessoku.bocchifrog.particles;

public class ThresheldParticleEffect extends LastingParticleEffect {

    private ThresholdParticleEvent[] thresholdEvents = new ThresholdParticleEvent[0];

    public ThresheldParticleEffect(float timeLeft) {
        super(timeLeft);
    }

    public void setEvents(ThresholdParticleEvent[] thresholdEvents) {
        this.thresholdEvents = thresholdEvents;
    }

    @Override
    public void update(float deltaTime, ParticleSystem particleSystem) {
        super.update(deltaTime, particleSystem);
        for (ThresholdParticleEvent thresholdEvent : thresholdEvents) {
            thresholdEvent.tryActivate(getTimeLeft(), particleSystem);
        }
    }
}
