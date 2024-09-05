package com.kessoku.bocchifrog.terrain;

import com.kessoku.bocchifrog.R;

public enum TerrainTileType {
    GRASS, WATER, ROAD, TRAIN_TRACKS, GOAL;

    public int getSpriteId() {
        switch (this) {
        case GRASS:
            return R.drawable.tile_grass;
        case WATER:
            return R.drawable.tile_water;
        case ROAD:
            return R.drawable.tile_road;
        case TRAIN_TRACKS:
            return R.drawable.tile_train_tracks;
        case GOAL:
            return R.drawable.tile_goal;
        default:
            throw new RuntimeException("Unrecognized TerrainTileType!");
        }
    }
}
