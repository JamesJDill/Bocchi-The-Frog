package com.kessoku.bocchifrog.rendering;

public class Rendering {

    // the number of tiles width-wise that will be visible on screen
    private static final int VISIBLE_TILE_WIDTH = 9;
    private static float tileSize;

    private static float viewWidth;
    private static float viewHeight;

    public static void setupForCanvasWidth(float viewWidth, float viewHeight) {
        Rendering.viewWidth = viewWidth;
        Rendering.viewHeight = viewHeight;
        Rendering.tileSize = viewWidth / VISIBLE_TILE_WIDTH;
    }

    public static float getTileSize() {
        return tileSize;
    }

    public static float getViewWidth() {
        return viewWidth;
    }

    public static float getViewHeight() {
        return viewHeight;
    }
}