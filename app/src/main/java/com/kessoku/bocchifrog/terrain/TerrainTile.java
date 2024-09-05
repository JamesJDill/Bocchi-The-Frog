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

public class TerrainTile extends Drawable {

    public static final int WATER_BONUS = 1;
    private final Point position;
    private final Bitmap sprite;
    private final TerrainTileType tileType;

    public TerrainTile(Point position, TerrainTileType tileType) {
        this.position = position;
        this.sprite = loadSprite(tileType);
        this.tileType = tileType;
    }

    private Bitmap loadSprite(TerrainTileType tileType) {
        return BitmapManager.getById(tileType.getSpriteId());
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

    public TerrainTileType getTileType() {
        return tileType;
    }
}
