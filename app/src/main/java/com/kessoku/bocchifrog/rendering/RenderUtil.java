package com.kessoku.bocchifrog.rendering;

import android.graphics.RectF;

import com.kessoku.bocchifrog.Vector2F;

public class RenderUtil {
    public static RectF sizedRectF(float left, float top, float width, float height) {
        return new RectF(left, top, left + width, top + height);
    }

    public static RectF tileRenderRectF(float tileX, float tileY,
                                        float tilesWide, float tilesHigh) {
        Vector2F tileOffset = Camera.getMainCamera().getTileOffset();

        float tileSize = Rendering.getTileSize();
        return sizedRectF(
                (tileX + tileOffset.getX()) * tileSize,
                (tileY - tileOffset.getY()) * tileSize,
                tileSize * tilesWide,
                tileSize * tilesHigh
        );
    }

    public static RectF tileRenderRectF(float tileX, float tileY) {
        return tileRenderRectF(tileX, tileY, 1.0f, 1.0f);
    }
}
