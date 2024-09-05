package com.kessoku.bocchifrog.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.entities.vehicles.EntityDirection;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;
import com.kessoku.bocchifrog.terrain.WorldMap;

public abstract class DirectionalEntity {
    private static final float OFF_SCREEN_BUFFER = 0.5f;

    private Vector2F position;
    private EntityDirection direction;

    public DirectionalEntity() {
        position = new Vector2F(0.0f, 0.0f);
    }

    public void place(int rowY, EntityDirection direction, WorldMap worldMap) {
        this.direction = direction;
        position = calcStartingPosition(rowY, getLength(), worldMap);
    }

    private Vector2F calcStartingPosition(int rowY, float length, WorldMap worldMap) {
        return new Vector2F(
                facingRight()
                        ? -(length + OFF_SCREEN_BUFFER)
                        : worldMap.getTileWidth() + OFF_SCREEN_BUFFER,
                rowY + 0.5f - getHeight() / 2.0f);
    }

    public void update(WorldMap map, float deltaTime, Player player) {
        // horizontal movement
        float diffX = calcVelocityX() * deltaTime;
        position.setX(position.getX() + diffX);

        // check for player collision
        if (collidesWith(player)) {
            onPlayerCollision(map, deltaTime, player);
        }
    }

    protected abstract void onPlayerCollision(WorldMap map, float deltaTime, Player player);

    public boolean shouldDeSpawn(WorldMap worldMap) {
        float x = position.getX();
        return facingRight()
                ? x > worldMap.getTileWidth() + OFF_SCREEN_BUFFER
                : x < -(getLength() + OFF_SCREEN_BUFFER);
    }

    private boolean collidesWithOffset(float offsetX,
                                       float otherX, float otherY,
                                       float otherWidth, float otherHeight) {
        float x = position.getX() + offsetX;
        float y = position.getY();
        float width = getLength();
        float height = getHeight();

        return !(
                x + width <= otherX
                        || x >= otherX + otherWidth
                        || y + height <= otherY
                        || y >= otherY + otherHeight);
    }


    public boolean collidesWith(float otherX, float otherY, float otherWidth, float otherHeight) {
        return collidesWithOffset(0, otherX, otherY, otherWidth, otherHeight);
    }

    public boolean collidesWithPlayerAt(Vector2F playerAt, float myOffset) {
        float playerColliderSize = 1.0f - getPlayerCollisionLeeway() * 2;

        return collidesWithOffset(myOffset,
                playerAt.getX() + getPlayerCollisionLeeway(),
                playerAt.getY() + getPlayerCollisionLeeway(),
                playerColliderSize, playerColliderSize);
    }

    public boolean collidesWith(Player player) {
        return collidesWithPlayerAt(player.getPosition(), 0);
    }


    public boolean collidesWith(DirectionalEntity entity) {
        return collidesWith(entity.getPosition().getX(), entity.getPosition().getY(),
                entity.getLength(), entity.getHeight());
    }

    public void draw(@NonNull Canvas canvas) {
        drawSelf(canvas);
    }

    protected void drawSelf(@NonNull Canvas canvas) {
        // draw vehicle sprite
        Bitmap sprite = BitmapManager.getById(findSpriteId());
        canvas.drawBitmap(
                sprite, null, calcRenderRect(), new Paint()
        );
    }

    private RectF calcRenderRect() {
        return RenderUtil.tileRenderRectF(
            position.getX(), position.getY(),
            getLength(), getHeight()
        );
    }

    public float calcVelocityX() {
        return getSpeed() * direction.movementSign();
    }

    protected boolean facingRight() {
        return direction == EntityDirection.RIGHT;
    }

    public Vector2F getPosition() {
        return position;
    }

    public EntityDirection getDirection() {
        return direction;
    }

    protected abstract int findSpriteId();
    protected abstract float getSpeed();
    protected abstract float getLength();
    protected float getHeight() {
        return 1.0f;
    }
    public abstract float getSpawningPeriod();
    public int getPointBonus() {
        return 0;
    }

    public abstract boolean shouldDrawOnTop();

    public abstract float getPlayerCollisionLeeway();
}
