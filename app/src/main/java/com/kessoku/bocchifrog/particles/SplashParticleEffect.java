package com.kessoku.bocchifrog.particles;

import android.graphics.Color;

import com.kessoku.bocchifrog.Randomness;
import com.kessoku.bocchifrog.Vector2F;

import java.util.function.Supplier;

public class SplashParticleEffect extends ThresheldParticleEffect {
    private static final float MAX_RIPPLE_SPEED = 0.75f;
    private static final int MAX_RIPPLE_PARTICLES = 100;

    private final Vector2F position;

    public SplashParticleEffect(Vector2F position) {
        super(2.0f);

        setEvents(new ThresholdParticleEvent[]{
            new ThresholdParticleEvent(2.0f, this::initialImpact),
            new ThresholdParticleEvent(1.75f, this::primaryRipple),
            new ThresholdParticleEvent(1.25f, this::secondaryRipple),
            new ThresholdParticleEvent(0.75f, this::tertiaryRipple)
        });

        this.position = position;
    }

    private void initialImpact(ParticleSystem particleSystem) {
        radialRipple(
                particleSystem,
                200,
                0.0f, 0.5f,
                0.9f, 0.5f,
                () -> createColorF(
                        0.2f,
                        Randomness.rand(0.5f, 0.6f),
                        Randomness.rand(0.95f, 1.0f),
                        1.0f
                )
        );
    }

    private void primaryRipple(ParticleSystem particleSystem) {
        radialRipple(
            particleSystem,
            MAX_RIPPLE_PARTICLES,
            0.05f, 0.015f,
            MAX_RIPPLE_SPEED, 0.1f,
            () -> createColorF(1.0f, 1.0f, 1.0f, 0.6f)
        );
    }

    private void secondaryRipple(ParticleSystem particleSystem) {
        radialRipple(
                particleSystem,
                MAX_RIPPLE_PARTICLES * 2 / 3,
                0.0f, 0.0015f,
                MAX_RIPPLE_SPEED, 0.005f,
                () -> createColorF(1.0f, 1.0f, 1.0f, 0.5f)
        );
    }

    private void tertiaryRipple(ParticleSystem particleSystem) {
        radialRipple(
                particleSystem,
                MAX_RIPPLE_PARTICLES / 3,
                0.0f, 0.0005f,
                MAX_RIPPLE_SPEED / 2, 0.0015f,
                () -> createColorF(1.0f, 1.0f, 1.0f, 0.3f)
        );
    }

    private void radialRipple(
            ParticleSystem particleSystem,
            int particleCount,
            float baseRad, float radPosVariance,
            float baseSpeed, float speedPosVariance,
            Supplier<Color> colorSupplier
    ) {
        for (int i = 0; i < particleCount; i++) {
            float angle = Randomness.rand((float) Math.PI * 2.0f);
            Vector2F dirVec = new Vector2F((float) Math.cos(angle), (float) Math.sin(angle));

            float rad = baseRad + Randomness.rand(radPosVariance);
            float speed = baseSpeed + Randomness.rand(speedPosVariance);

            Particle particle = new PixelParticle(
                    position.displacedBy(dirVec.scaledBy(rad)),
                    dirVec.scaledBy(speed),
                    argbFromColor(colorSupplier.get()),
                    Randomness.rand(1.0f, 1.5f)
            );

            particleSystem.add(particle);
        }
    }
}
