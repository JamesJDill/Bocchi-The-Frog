package com.kessoku.bocchifrog.entities.vehicles;

import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.terrain.WorldMap;

import java.util.function.Supplier;

public class SpawningLane {
    private final int rowY;
    private final EntityDirection laneDirection;
    private float timeTillNextSpawn = 0.0f;
    private final Supplier<DirectionalEntity> vehicleSupplier;
    private final int pointBonus;

    public SpawningLane(int rowY, EntityDirection laneDirection,
                        Supplier<DirectionalEntity> vehicleSupplier) {
        this.rowY = rowY;
        this.laneDirection = laneDirection;
        this.vehicleSupplier = vehicleSupplier;
        this.pointBonus = vehicleSupplier.get().getPointBonus();
    }

    public void updateSpawning(float deltaTime, WorldMap worldMap) {
        // tick down spawning cool-down
        timeTillNextSpawn -= deltaTime;

        if (timeTillNextSpawn <= 0.0f) {
            // spawn vehicle
            DirectionalEntity spawned = vehicleSupplier.get();
            spawned.place(rowY, laneDirection, worldMap);
            worldMap.addEntity(spawned);

            // reset spawning period
            timeTillNextSpawn = spawned.getSpawningPeriod();
        }
    }

    public int getPointBonus() {
        return pointBonus;
    }
}
