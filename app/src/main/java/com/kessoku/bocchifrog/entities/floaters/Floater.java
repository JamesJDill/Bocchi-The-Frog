package com.kessoku.bocchifrog.entities.floaters;

import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.terrain.WorldMap;

public abstract class Floater extends DirectionalEntity {
    public float nearestNodeXPositionAfter(Vector2F targetPos, float deltaTime) {
        float xAfterTime = getPosition().getX() + calcVelocityX() * deltaTime;
        int nearestNode = Math.round(targetPos.getX() - xAfterTime);
        int nearestNodeClamped = Math.max(0, Math.min(nearestNode, (int) (getLength() - 1)));
        return xAfterTime + nearestNodeClamped;
    }

    @Override
    protected void onPlayerCollision(WorldMap map, float deltaTime, Player player) {
        if (isSurfaced() && player.shouldRideFloater()) {
            player.rideFloater(map, calcVelocityX() * deltaTime);
        }
    }

    @Override
    public boolean shouldDrawOnTop() {
        return false;
    }

    @Override
    public float getPlayerCollisionLeeway() {
        return 0;
    }

    protected boolean isSurfaced() {
        return true;
    }
}
