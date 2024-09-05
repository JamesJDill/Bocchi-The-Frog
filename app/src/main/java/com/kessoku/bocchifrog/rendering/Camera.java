package com.kessoku.bocchifrog.rendering;

import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.terrain.WorldMap;

public class Camera {
    private Vector2F tileOffset;
    private static Camera mainCamera;
    private final WorldMap attachedMap;
    private boolean initialized = false;

    public Camera(WorldMap attachedMap) {
        tileOffset = new Vector2F(0, 0);
        this.attachedMap = attachedMap;
    }

    public void setAsMain() {
        mainCamera = this;
    }

    public void update(Vector2F followTargetPosition, float deltaTime) {
        approachTarget(calcTargetTileY(followTargetPosition),
                0, findOffsetMax(attachedMap), deltaTime);
    }

    private float calcTargetTileY(Vector2F followTargetPosition) {
        float midScreenTile = Rendering.getViewHeight() / Rendering.getTileSize() * 0.5f;
        return followTargetPosition.getY() - midScreenTile;
    }

    private void approachTarget(float targetTileY,
                                float minTileY, float maxTileY, float deltaTime) {
        if (initialized) {
            // step towards
            tileOffset.setY(tileOffset.getY() + (targetTileY - tileOffset.getY()) * deltaTime * 5);
        } else {
            // initialize to this value if the camera is new
            tileOffset.setY(targetTileY);
            initialized = true;
        }

        // clamp into bounds
        tileOffset.setY(Math.max(minTileY, Math.min(maxTileY, tileOffset.getY())));
    }

    public Vector2F getTileOffset() {
        return tileOffset;
    }

    public static Camera getMainCamera() {
        return mainCamera;
    }

    private float findOffsetMax(WorldMap worldMap) {
        return worldMap.getTileHeight()
                - (Rendering.getViewHeight() / Rendering.getTileSize());
    }
}
