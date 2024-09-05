package com.kessoku.bocchifrog.terrain;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;

public class Obstacle extends Drawable {
    private final Point position;
    private final Bitmap sprite;

    public Obstacle(Point position, ObstacleType obstacleType) {
        this.position = position;
        this.sprite = loadSprite(obstacleType);
    }

    private Bitmap loadSprite(ObstacleType obstacleType) {
        return BitmapManager.getById(obstacleType.getSpriteId());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(
                sprite,
                null,
                RenderUtil.tileRenderRectF(position.x, position.y),
                new Paint()
        );
    }

    @Override
    public void setAlpha(int i) { }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}