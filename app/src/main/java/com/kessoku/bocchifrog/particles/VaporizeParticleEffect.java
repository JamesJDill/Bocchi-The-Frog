package com.kessoku.bocchifrog.particles;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Randomness;
import com.kessoku.bocchifrog.Vector2F;

public class VaporizeParticleEffect extends ParticleEffect {
    private static final float VAPOR_EXPLOSION_RANDOMNESS = 0.65f;

    private final Bitmap sprite;
    private final Vector2F position;
    private final Vector2F baseVelocity;

    public VaporizeParticleEffect(Bitmap sprite, Vector2F position, Vector2F baseVelocity) {
        this.sprite = sprite;
        this.position = position;
        this.baseVelocity = baseVelocity;
    }

    @Override
    public void begin(@NonNull ParticleSystem particleSystem) {
        if (sprite == null) {
            return;
        }

        int pixelsX = 16;
        int pixelsY = 16;

        int pixelsPerParticleX = sprite.getWidth() / pixelsX;
        int pixelsPerParticleY = sprite.getHeight() / pixelsY;

        for (int pixelY = 0; pixelY < pixelsY; pixelY++) {
            for (int pixelX = 0; pixelX < pixelsX; pixelX++) {

                Vector2F pixelPosition = position.displacedBy(
                    new Vector2F(pixelX, pixelY).scaledBy(PixelParticle.PIXEL_SIZE)
                );

                Vector2F pixelVelocity = baseVelocity.displacedBy(
                    new Vector2F(
                        Randomness.randPosNeg(VAPOR_EXPLOSION_RANDOMNESS),
                        Randomness.randPosNeg(VAPOR_EXPLOSION_RANDOMNESS)
                    )
                );

                Color pixelColor = sprite.getColor(
                        pixelX * pixelsPerParticleX,
                        pixelY * pixelsPerParticleY
                );
                int pixelColorCode = argbFromColor(pixelColor);

                Particle particle = new PixelParticle(
                    pixelPosition, pixelVelocity,
                    pixelColorCode, Randomness.rand(1.5f, 2.0f)
                );

                particleSystem.add(particle);
            }
        }
    }
}
