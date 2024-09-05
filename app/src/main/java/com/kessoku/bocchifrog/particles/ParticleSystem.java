package com.kessoku.bocchifrog.particles;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Vector2F;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private final List<Particle> particles = new ArrayList<>();
    private final List<LastingParticleEffect> lastingParticleEffects = new ArrayList<>();

    private Vector2F lastAverageParticleLocation = new Vector2F(0, 0);

    public void update(float deltaTime) {
        for (int i = lastingParticleEffects.size() - 1; i >= 0; i--) {
            LastingParticleEffect lastingParticleEffect = lastingParticleEffects.get(i);

            lastingParticleEffect.update(deltaTime, this);

            if (lastingParticleEffect.shouldDelete()) {
                lastingParticleEffects.remove(i);
            }
        }

        float particleSumX = 0.0f;
        float particleSumY = 0.0f;

        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);

            particle.update(deltaTime);

            if (particle.shouldDeSpawn()) {
                particles.remove(i);
            } else {
                particleSumX += particle.getPosition().getX();
                particleSumY += particle.getPosition().getY();
            }
        }

        if (!particles.isEmpty()) {
            lastAverageParticleLocation = new Vector2F(particleSumX, particleSumY)
                    .scaledBy(1.0f / particles.size());
        }
    }

    public void draw(@NonNull Canvas canvas) {
        for (Particle particle : particles) {
            particle.draw(canvas);
        }
    }

    public void add(Particle particle) {
        particles.add(particle);
    }

    public void beginLastingParticleEffect(LastingParticleEffect lastingParticleEffect) {
        lastingParticleEffects.add(lastingParticleEffect);
    }

    public Vector2F getLastAverageParticleLocation() {
        return lastAverageParticleLocation;
    }
}
