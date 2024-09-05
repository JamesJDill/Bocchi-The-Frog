package com.kessoku.bocchifrog.entities.vehicles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;

public class Train extends Vehicle {

    private static final int TRAIN_LINK_COUNT = 24;

    @Override
    public void drawSelf(@NonNull Canvas canvas) {
        // draw TRAIN_LINK_COUNT train segments
        for (int i = 0; i < TRAIN_LINK_COUNT; i++) {
            RectF destRect = RenderUtil.tileRenderRectF(
                getPosition().getX() + i, getPosition().getY()
            );

            // draw vehicle sprite
            int spriteId = findSpriteId();
            if (i == 0) {
                spriteId = findLeftSpriteId();
            } else if (i == TRAIN_LINK_COUNT - 1) {
                spriteId = findRightSpriteId();
            }

            Bitmap sprite = BitmapManager.getById(spriteId);
            canvas.drawBitmap(
                sprite, null, destRect, new Paint()
            );
        }
    }

    @Override
    protected int findSpriteId() {
        return facingRight() ? R.drawable.train_middle_right : R.drawable.train_middle_left;
    }

    private int findLeftSpriteId() {
        return facingRight() ? R.drawable.train_back_right : R.drawable.train_front_left;
    }

    private int findRightSpriteId() {
        return facingRight() ? R.drawable.train_front_right : R.drawable.train_back_left;
    }

    @Override
    protected float getSpeed() {
        return 20.0f;
    }

    @Override
    protected float getLength() {
        return TRAIN_LINK_COUNT;
    }

    @Override
    public float getSpawningPeriod() {
        return 5.0f;
    }

    @Override
    public int getPointBonus() {
        return 4;
    }
}
