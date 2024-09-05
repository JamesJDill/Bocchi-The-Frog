package com.kessoku.bocchifrog.particles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;

public class PixelParticle extends TimedParticle {
    public static final float PIXEL_SIZE = 1.0f / 16.0f;
    private static final float FRICTION_COEFFICIENT = 3.0f;
    private static final float FADE_BEGIN_SECONDS = 0.5f;

    private final Vector2F velocity;
    private final int color;
    private final Bitmap sprite;

    public PixelParticle(Vector2F position, Vector2F velocity, int color, float time) {
        super(position, time);

        sprite = BitmapManager.getById(R.drawable.pixel);
        this.velocity = velocity;
        this.color = color;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        getPosition().displace(velocity.scaledBy(deltaTime));
        velocity.scale(1 - (deltaTime * FRICTION_COEFFICIENT));
    }

    private float getAlpha() {
        if (getTimeLeft() < FADE_BEGIN_SECONDS) {
            return getTimeLeft() / FADE_BEGIN_SECONDS;
        }
        return 1.0f;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        RectF destRect = RenderUtil.tileRenderRectF(getPosition().getX(), getPosition().getY(),
                PIXEL_SIZE, PIXEL_SIZE);

        Paint tintPaint = new Paint();

        float currPixelAlpha = (float) ((color & 0xFF000000) >>> 24) / 0xFF;
        int colorWithAlpha = color
                & 0xFFFFFF
                | ((int) (0xFF * getAlpha() * currPixelAlpha) << 24);
        tintPaint.setColorFilter(new PorterDuffColorFilter(colorWithAlpha, PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(
                sprite, null, destRect, tintPaint
        );
    }
}
