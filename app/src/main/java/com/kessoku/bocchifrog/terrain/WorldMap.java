package com.kessoku.bocchifrog.terrain;

import android.graphics.Canvas;
import android.graphics.Point;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.entities.vehicles.Bike;
import com.kessoku.bocchifrog.entities.vehicles.Taxi;
import com.kessoku.bocchifrog.entities.vehicles.Train;
import com.kessoku.bocchifrog.entities.vehicles.Truck;
import com.kessoku.bocchifrog.entities.vehicles.EntityDirection;
import com.kessoku.bocchifrog.entities.vehicles.SpawningLane;
import com.kessoku.bocchifrog.entities.floaters.Log;
import com.kessoku.bocchifrog.entities.floaters.TurtleChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class WorldMap {
    public static final int DEFAULT_MAP_WIDTH = 9;
    public static final int DEFAULT_MAP_HEIGHT = 25;
    public static final int PLAYER_SPAWN_X = 4;
    public static final int PLAYER_SPAWN_Y = 22;

    private final TerrainTile[][] tileGrid;
    private final Obstacle[][] obstacleGrid;
    private final List<DirectionalEntity> entities;
    private final HashMap<Integer, SpawningLane> laneMap;

    private final int tileWidth;
    private final int tileHeight;

    public WorldMap(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        entities = new ArrayList<>();
        laneMap = new HashMap<>();

        // initialize map with grass tiles and no obstacles
        tileGrid = new TerrainTile[tileHeight][];
        obstacleGrid = new Obstacle[tileHeight][];
        for (int tileY = 0; tileY < tileHeight; tileY++) {
            TerrainTile[] row = new TerrainTile[tileWidth];
            for (int tileX = 0; tileX < tileWidth; tileX++) {
                row[tileX] = new TerrainTile(
                    new Point(tileX, tileY),
                    TerrainTileType.GRASS
                );
            }
            tileGrid[tileY] = row;
            obstacleGrid[tileY] = new Obstacle[tileWidth];
        }
    }

    public WorldMap() {
        this(DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT);
    }

    public void addEntity(DirectionalEntity entity) {
        entities.add(entity);
    }

    public void updateWorld(float deltaTime, Player player) {
        updateEntities(deltaTime, player);
        updateEntitySpawning(deltaTime);
    }

    private void updateEntities(float deltaTime, Player player) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            DirectionalEntity entity = entities.get(i);

            entity.update(this, deltaTime, player);
            // de-spawn old off-screen entities
            if (entity.shouldDeSpawn(this)) {
                entities.remove(i);
            }
        }
    }

    private void updateEntitySpawning(float deltaTime) {
        for (SpawningLane lane : laneMap.values()) {
            lane.updateSpawning(deltaTime, this);
        }
    }

    public void addRows(TerrainTileType type, int tileY, int size) {
        int maxY = Math.min(tileHeight, tileY + size);
        for (int y = tileY; y < maxY; y++) {
            for (int tileX = 0; tileX < tileWidth; tileX++) {
                tileGrid[y][tileX] = new TerrainTile(
                        new Point(tileX, y),
                        type
                );
            }
        }
    }

    public void addTrainTracks(int rowY, EntityDirection direction) {
        addRows(TerrainTileType.TRAIN_TRACKS, rowY, 1);
        laneMap.put(rowY, new SpawningLane(rowY, direction, Train::new));
    }

    public void addLane(int rowY, EntityDirection direction,
                        Supplier<DirectionalEntity> vehicleSupplier) {
        addRows(TerrainTileType.ROAD, rowY, 1);
        laneMap.put(rowY, new SpawningLane(rowY, direction, vehicleSupplier));
    }

    public void addRiverLane(int rowY, EntityDirection direction,
                         Supplier<DirectionalEntity> floaterSupplier) {
        addRows(TerrainTileType.WATER, rowY, 1);
        laneMap.put(rowY, new SpawningLane(rowY, direction, floaterSupplier));
    }


    public void addRock(int tileX, int tileY) {
        obstacleGrid[tileY][tileX] = new Obstacle(new Point(tileX, tileY), ObstacleType.ROCK);
    }

    public void addRiver(int startY, int endY) {
        addRows(TerrainTileType.WATER, startY, endY - startY + 1);
    }

    public void drawBackground(@NonNull Canvas canvas) {
        for (int tileY = 0; tileY < tileHeight; tileY++) {
            for (int tileX = 0; tileX < tileWidth; tileX++) {
                TerrainTile tile = tileGrid[tileY][tileX];

                tile.draw(canvas);
            }
        }
    }

    public void drawMidGround(@NonNull Canvas canvas) {
        for (DirectionalEntity entity : entities) {
            if (!entity.shouldDrawOnTop()) {
                entity.draw(canvas);
            }
        }
    }

    public void drawForeground(@NonNull Canvas canvas) {
        for (int tileY = 0; tileY < tileHeight; tileY++) {
            for (int tileX = 0; tileX < tileWidth; tileX++) {
                Obstacle obstacle = obstacleGrid[tileY][tileX];

                if (obstacle != null) {
                    obstacle.draw(canvas);
                }
            }
        }

        for (DirectionalEntity entity : entities) {
            if (entity.shouldDrawOnTop()) {
                entity.draw(canvas);
            }
        }
    }

    public TerrainTileType tileTypeAt(int tileX, int tileY) {
        if (tileX < 0 || tileX >= tileWidth || tileY < 0 || tileY >= tileHeight) {
            return TerrainTileType.GRASS;
        }
        return tileGrid[tileY][tileX].getTileType();
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public static WorldMap generateGameMap() {
        return new WorldMap() {
            {
                addRows(TerrainTileType.GOAL, 0, 1);

                addTrainTracks(2, EntityDirection.LEFT);
                addTrainTracks(3, EntityDirection.RIGHT);

                addRiverLane(5, EntityDirection.LEFT, TurtleChain::new);
                addRiverLane(6, EntityDirection.RIGHT, TurtleChain::new);
                addRiverLane(7, EntityDirection.RIGHT, Log::new);

                addLane(9, EntityDirection.RIGHT, Bike::new);
                addLane(10, EntityDirection.LEFT, Taxi::new);
                addLane(11, EntityDirection.LEFT, Truck::new);

                addTrainTracks(13, EntityDirection.LEFT);
                addTrainTracks(14, EntityDirection.RIGHT);

                addRiverLane(16, EntityDirection.LEFT, Log::new);
                addRiverLane(17, EntityDirection.LEFT, TurtleChain::new);

                addTrainTracks(19, EntityDirection.LEFT);
                addLane(20, EntityDirection.LEFT, Taxi::new);
                addLane(21, EntityDirection.RIGHT, Bike::new);

                addRiverLane(24, EntityDirection.RIGHT, TurtleChain::new);

                addRock(6, 1);
                addRock(1, 4);
                addRock(7, 15);
                addRock(3, 18);
                addRock(8, 22);

                // TODO: delete once floater collision is implemented
//                addRows(TerrainTileType.GOAL, 18, 1);
            }
        };
    }

    public HashMap<Integer, SpawningLane> getLaneMap() {
        return laneMap;
    }

    public List<DirectionalEntity> getEntities() {
        return entities;
    }
}
