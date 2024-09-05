package com.kessoku.bocchifrog.entities.vehicles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;
import com.kessoku.bocchifrog.terrain.WorldMap;

public abstract class Vehicle extends DirectionalEntity {
    @Override
    protected void onPlayerCollision(WorldMap map, float deltaTime, Player player) {
        runOver(player);
    }

    private void runOver(Player player) {
        player.dieViaCrushing(new Vector2F(getSpeed() * getDirection().movementSign(), 0));
    }

    @Override
    public final void draw(@NonNull Canvas canvas) {
        // draw shadow
        Bitmap shadowBitmap = BitmapManager.getById(R.drawable.shadow);
        canvas.drawBitmap(
                shadowBitmap, null, calcShadowRenderRect(), new Paint()
        );

        drawSelf(canvas);
    }

    private RectF calcShadowRenderRect() {
        float shadowPad = getShadowExpansionPadding();
        return RenderUtil.tileRenderRectF(
                getPosition().getX() - shadowPad,
                getPosition().getY() - shadowPad,
                getLength() + shadowPad * 2.0f,
                getHeight() + shadowPad * 2.0f
        );
    }

    public float getShadowExpansionPadding() {
        return 0.0f;
    }

    @Override
    public boolean shouldDrawOnTop() {
        return true;
    }

    @Override
    public float getPlayerCollisionLeeway() {
        return 3.0f / 16.0f;
    }
}
