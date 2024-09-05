package com.kessoku.bocchifrog;
import static com.kessoku.bocchifrog.MainActivity.generateNameValidationText;

import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;

import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.entities.floaters.Log;
import com.kessoku.bocchifrog.entities.floaters.TurtleChain;
import com.kessoku.bocchifrog.player.Difficulty;
import com.kessoku.bocchifrog.player.MovementDirection;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.player.PlayerCharacter;
import com.kessoku.bocchifrog.player.PlayerConfig;
import com.kessoku.bocchifrog.terrain.TerrainTileType;
import com.kessoku.bocchifrog.terrain.WorldMap;
import com.kessoku.bocchifrog.entities.vehicles.Bike;
import com.kessoku.bocchifrog.entities.vehicles.Taxi;
import com.kessoku.bocchifrog.entities.vehicles.Truck;
import com.kessoku.bocchifrog.entities.vehicles.Train;
import com.kessoku.bocchifrog.entities.vehicles.EntityDirection;

public class BocchiTests {
    private static final int TIMEOUT = 200;

    private static final int BASE_ADVANCEMENT_POINT_VALUE = 1;
    private static final int BIKE_LANE_BONUS_POINTS = 1;
    private static final int TAXI_LANE_BONUS_POINTS = 2;
    private static final int TRUCK_LANE_BONUS_POINTS = 3;
    private static final int TRAIN_LANE_BONUS_POINTS = 4;
    private static final int WATER_BONUS_POINTS = 1;

    /* Helper Methods =========================================================================== */

    private static PlayerConfig createDefaultPlayerConfig(Difficulty difficulty) {
        return new PlayerConfig("John Doe", PlayerCharacter.BOCCHI, difficulty);
    }

    private static PlayerConfig createDefaultPlayerConfig() {
        return createDefaultPlayerConfig(Difficulty.NORMAL);
    }

    private static Game createInitializedGameWithDifficulty(Difficulty difficulty) {
        return createInitializedGame(createDefaultPlayerConfig(difficulty));
    }

    private static Game createDefaultInitializedGame() {
        return createInitializedGameWithDifficulty(Difficulty.NORMAL);
    }

    private static Game createInitializedGame(PlayerConfig playerConfig) {
        Game game = new Game();
        game.initialize(playerConfig, new WorldMap());
        return game;
    }

    private static Game createGameWithMapAndDifficulty(
            WorldMap worldMap, Difficulty difficulty) {
        Game game = new Game();
        game.initialize(createDefaultPlayerConfig(difficulty), worldMap);
        return game;
    }

    private static Game createGameWithMap(WorldMap worldMap) {
        return createGameWithMapAndDifficulty(worldMap, Difficulty.NORMAL);
    }

    private static void updateGameFor(Game game, float seconds) {
        game.updateInstantlyFor(seconds);
    }

    private static void updateGameUntil(Game game, Supplier<Boolean> condition) {
        while (!condition.get()) {
            game.update(Game.EXPECTED_FRAME_TIME);
        }
    }

    private static void updateGameWhileChecking(Game game, float seconds, Runnable checkFunc) {
        for (float time = 0.0f; time < seconds; time += Game.EXPECTED_FRAME_TIME) {
            game.update(Game.EXPECTED_FRAME_TIME);
            checkFunc.run();
        }
    }

    private static void waitForJumps(Game game, Player player,
                                     MovementDirection jumpDirection, int jumpCount) {
        for (int i = 0; i < jumpCount; i++) {
            player.inputJumpMovement(game.getMap(), jumpDirection);
            updateGameFor(game, Player.TOTAL_JUMP_TIME + 0.1f);
        }
    }


    private static void waitForJump(Game game, Player player, MovementDirection jumpDirection) {
        waitForJumps(game, player, jumpDirection, 1);
    }

    private void assertVectorEquals(Vector2F a, Vector2F b, float deltaXY) {
        assertEquals(a.getX(), b.getX(), deltaXY);
        assertEquals(a.getY(), b.getY(), deltaXY);
    }

    private void assertVectorEquals(Vector2F a, Vector2F b) {
        assertVectorEquals(a, b, 0.01f);
    }

    private WorldMap createScoreTestingMap() {
        return new WorldMap() {
            {
                // player starts at y = 22, heading to y = 0
                addLane(21, EntityDirection.LEFT, Bike::new);
                addLane(20, EntityDirection.RIGHT, Taxi::new);
                addLane(19, EntityDirection.LEFT, Truck::new);
                addTrainTracks(18, EntityDirection.RIGHT);
                addRiver(17, 17);
                addRows(TerrainTileType.GOAL, 15, 1);
            }
        };
    }

    private WorldMap createFloaterTestingMap(EntityDirection direction,
                                             Supplier<DirectionalEntity> supplier) {
        return new WorldMap() {
            {
                // player starts at y = 22, heading to y = 0
                addRiverLane(21, direction, supplier);
            }
        };
    }

    private void testSpawningAndDeSpawningInDirection(
            Supplier<DirectionalEntity> entitySupplier, EntityDirection direction,
            int minEntitiesPerLane, int maxEntitiesPerLane) {
        Game game = createGameWithMap(new WorldMap() {
            {
                addLane(21, direction, entitySupplier);
            }
        });
        WorldMap worldMap = game.getMap();


        final RunnableTestChecker vehicleLaneChecker = new RunnableTestChecker() {
            private boolean spawnSuccessful = false;
            private boolean deSpawnSuccessful = false;
            private List<DirectionalEntity> previousVehicles = null;
            @Override
            public void run() {
                List<DirectionalEntity> vehicles = worldMap.getEntities();

                assertTrue(vehicles.size() >= minEntitiesPerLane
                        && vehicles.size() <= maxEntitiesPerLane);

                if (previousVehicles != null) {
                    // check spawns
                    for (DirectionalEntity vehicle : vehicles) {
                        if (!previousVehicles.contains(vehicle)) {
                            onSpawn();
                        }
                    }
                    // check de-spawns
                    for (DirectionalEntity previousVehicle : previousVehicles) {
                        if (!vehicles.contains(previousVehicle)) {
                            onDeSpawn();
                        }
                    }
                } else {
                    previousVehicles = new ArrayList<>();
                }

                previousVehicles.clear();
                previousVehicles.addAll(vehicles);
            }

            private void onSpawn() {
                spawnSuccessful = true;
            }

            private void onDeSpawn() {
                deSpawnSuccessful = true;
            }

            @Override
            public void checkSuccess() {
                assertTrue(spawnSuccessful);
                assertTrue(deSpawnSuccessful);
            }
        };

        updateGameWhileChecking(game, 20.0f, vehicleLaneChecker);
        vehicleLaneChecker.checkSuccess();
    }

    private void testSpawningAndDeSpawning(
            Supplier<DirectionalEntity> entitySupplier,
            int minEntitiesPerLane, int maxEntitiesPerLane) {
        // test in both directions
        testSpawningAndDeSpawningInDirection(entitySupplier, EntityDirection.LEFT,
                minEntitiesPerLane, maxEntitiesPerLane);
        testSpawningAndDeSpawningInDirection(entitySupplier, EntityDirection.RIGHT,
                minEntitiesPerLane, maxEntitiesPerLane);
    }

    private static Log findLog(WorldMap map) {
        for (DirectionalEntity entity : map.getEntities()) {
            if (entity instanceof Log) {
                return (Log) entity;
            }
        }
        return null;
    }

    private static TurtleChain findTurtleChain(WorldMap map) {
        for (DirectionalEntity entity : map.getEntities()) {
            if (entity instanceof TurtleChain) {
                return (TurtleChain) entity;
            }
        }
        return null;
    }

    /* Test Setup =============================================================================== */
    @Before
    public void setupTestRendering() {
        Game.disableDynamicParticleColouring();
    }

    /* Sprint 5 Tests =========================================================================== */

    // the player should gain 50 additional points
    //     for jumping onto the goal tile
    @Test(timeout = TIMEOUT)
    public void testGoalTilePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 7;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * (totalJumps + 1)
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS + TRUCK_LANE_BONUS_POINTS
                + TRAIN_LANE_BONUS_POINTS + 50);
    }

    // make sure that logs and turtle-chains have different speeds!
    @Test(timeout = TIMEOUT)
    public void testLogAndTurtleChainDifferingSpeeds() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addRiverLane(21, EntityDirection.LEFT, Log::new);
                addRiverLane(20, EntityDirection.LEFT, TurtleChain::new);
            }
        });
        WorldMap map = game.getMap();

        Log log = findLog(map);
        TurtleChain turtleChain = findTurtleChain(map);

        assertNotNull(log);
        assertNotNull(turtleChain);
        assertNotEquals(log.calcVelocityX(), turtleChain.calcVelocityX());
    }

    @Test(timeout = TIMEOUT)
    public void testDeathFromFlowingOffScreenLeftwardsOnLog() {
        Game game = createGameWithMap(createFloaterTestingMap(EntityDirection.LEFT, Log::new));
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        updateGameUntil(game, () -> player.hasFloaterAhead(game.getMap()));

        waitForJump(game, player, MovementDirection.UP);
        updateGameFor(game, 20.0f);

        int finalLives = player.getLivesRemaining();

        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testDeathFromFlowingOffScreenRightwardsOnLog() {
        Game game = createGameWithMap(createFloaterTestingMap(EntityDirection.RIGHT, Log::new));
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        updateGameUntil(game, () -> player.hasFloaterAhead(game.getMap()));

        waitForJump(game, player, MovementDirection.UP);
        updateGameFor(game, 20.0f);

        int finalLives = player.getLivesRemaining();

        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testDodgingTurtleChainCausesDeath() {
        Game game = createGameWithMap(
                createFloaterTestingMap(EntityDirection.LEFT, TurtleChain::new));
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        updateGameUntil(game, () -> !player.hasFloaterAhead(game.getMap()));

        waitForJump(game, player, MovementDirection.UP);

        int finalLives = player.getLivesRemaining();

        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testLandingOnTurtleChainSavesPlayer() {
        Game game = createGameWithMap(
                createFloaterTestingMap(EntityDirection.LEFT, TurtleChain::new));
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        updateGameUntil(game, () -> player.hasFloaterAhead(game.getMap()));

        waitForJump(game, player, MovementDirection.UP);

        int finalLives = player.getLivesRemaining();

        assertEquals(finalLives, initialLives);
    }
    
    /* Sprint 4 Tests =========================================================================== */
    @Test(timeout = TIMEOUT)
    public void testDeathReSpawnPoint() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addRiver(15, 17);
            }
        });
        Player player = game.getPlayer();

        Vector2F initialPlayerPosition = new Vector2F(
                player.getPosition().getX(),
                player.getPosition().getY()
        );

        waitForJumps(game, player, MovementDirection.UP, 5);

        Vector2F finalPlayerPosition = new Vector2F(
                player.getPosition().getX(),
                player.getPosition().getY()
        );

        // check that we are in the same place as we started
        // after jumping into the water 5 blocks away
        assertVectorEquals(initialPlayerPosition, finalPlayerPosition, 0.01f);
    }

    @Test(timeout = TIMEOUT)
    public void testHalvingScoreOnDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addRiver(15, 17);
            }
        });
        Player player = game.getPlayer();

        waitForJumps(game, player, MovementDirection.UP, 5);

        // the player has gained 5 points by jumping from 5 normal tiles
        // but dying on collision with the water will half their points, leaving 2
        assertEquals(player.getPoints(), 2);
    }

    @Test(timeout = TIMEOUT)
    public void testWaterDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addRiver(19, 21);
            }
        });
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        waitForJump(game, player, MovementDirection.UP);

        int finalLives = player.getLivesRemaining();

        // check that we immediately lost a life after jumping into the river
        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testTaxiDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addLane(21, EntityDirection.RIGHT, Taxi::new);
            }
        });
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        // jump onto lane
        waitForJump(game, player, MovementDirection.UP);

        // wait at least 2 seconds to ensure we are hit by a taxi
        updateGameFor(game, 2);

        int finalLives = player.getLivesRemaining();

        // check that we lost a life after being hit by a taxi
        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testTruckDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addLane(21, EntityDirection.RIGHT, Truck::new);
            }
        });
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        // jump onto lane
        waitForJump(game, player, MovementDirection.UP);

        // wait at least 2 seconds to ensure we are hit by a truck
        updateGameFor(game, 2);

        int finalLives = player.getLivesRemaining();

        // check that we lost a life after being hit by a truck
        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testBikeDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addLane(21, EntityDirection.RIGHT, Bike::new);
            }
        });
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        // jump onto lane
        waitForJump(game, player, MovementDirection.UP);

        // wait at least 2 seconds to ensure we are hit by a bike
        updateGameFor(game, 2);

        int finalLives = player.getLivesRemaining();

        // check that we lost a life after being hit by a bike
        assertEquals(finalLives, initialLives - 1);
    }
    
    @Test(timeout = TIMEOUT)
    public void testTrainDeath() {
        Game game = createGameWithMap(new WorldMap() {
            {
                addLane(21, EntityDirection.RIGHT, Train::new);
            }
        });
        Player player = game.getPlayer();

        int initialLives = player.getLivesRemaining();

        // jump onto lane
        waitForJump(game, player, MovementDirection.UP);

        // wait at least 5 seconds to ensure we are hit by a train
        updateGameFor(game, 5);

        int finalLives = player.getLivesRemaining();

        // check that we lost a life after being hit by a train
        assertEquals(finalLives, initialLives - 1);
    }

    @Test(timeout = TIMEOUT)
    public void testGameOverEasyMode() {
        Game game = createGameWithMapAndDifficulty(new WorldMap() {
            {
                addRiver(19, 21);
            }
        }, Difficulty.EASY);
        Player player = game.getPlayer();

        for (int i = 0; i < 5; i++) {
            waitForJump(game, player, MovementDirection.UP);
            updateGameFor(game, 3); // wait for respawn
        }

        // check that the game is over after dying 5 times on easy mode
        assertTrue(game.isGameOver());
    }

    @Test(timeout = TIMEOUT)
    public void testGameOverNormalMode() {
        Game game = createGameWithMapAndDifficulty(new WorldMap() {
            {
                addRiver(19, 21);
            }
        }, Difficulty.NORMAL);
        Player player = game.getPlayer();

        for (int i = 0; i < 3; i++) {
            waitForJump(game, player, MovementDirection.UP);
            updateGameFor(game, 3); // wait for respawn
        }

        // check that the game is over after dying 3 times on normal mode
        assertTrue(game.isGameOver());
    }

    @Test(timeout = TIMEOUT)
    public void testGameOverHardMode() {
        Game game = createGameWithMapAndDifficulty(new WorldMap() {
            {
                addRiver(19, 21);
            }
        }, Difficulty.HARD);
        Player player = game.getPlayer();

        waitForJump(game, player, MovementDirection.UP);

        // check that the game is over after dying once on hard mode
        assertTrue(game.isGameOver());
    }

    
    /* Sprint 3 Tests =========================================================================== */

    // check that bikes are able to spawn and de-spawn,
    //     and that their quantity remains within the expected range
    @Test(timeout = TIMEOUT)
    public void testBikeSpawningAndDeSpawning() {
        testSpawningAndDeSpawning(Bike::new, 2, 3);
    }

    // check that taxis are able to spawn and de-spawn,
    //     and that their quantity remains within the expected range
    @Test(timeout = TIMEOUT)
    public void testTaxiSpawningAndDeSpawning() {
        testSpawningAndDeSpawning(Taxi::new, 1, 2);
    }

    // check that trucks are able to spawn and de-spawn,
    //     and that their quantity remains within the expected range
    @Test(timeout = TIMEOUT)
    public void testTruckSpawningAndDeSpawning() {
        testSpawningAndDeSpawning(Truck::new, 1, 2);
    }

    // check that trains are able to spawn and de-spawn,
    //     and that their quantity remains within the expected range
    @Test(timeout = TIMEOUT)
    public void testTrainSpawningAndDeSpawning() {
        testSpawningAndDeSpawning(Train::new, 0, 1);
    }

    // run the default game screen for 20 seconds and check that there are no vehicle collisions
    @Test(timeout = TIMEOUT)
    public void testNoVehicleCollision() {
        Game game = createDefaultInitializedGame();
        WorldMap map = game.getMap();
        game.getPlayer().setInvincible(true); // make sure player doesn't die for test

        updateGameWhileChecking(game, 20.0f, () -> {
            List<DirectionalEntity> vehicles = map.getEntities();
            for (DirectionalEntity vehicle : vehicles) {
                for (DirectionalEntity otherVehicle : vehicles) {
                    assertTrue(vehicle == otherVehicle || !vehicle.collidesWith(otherVehicle));
                }
            }
        });
    }


    /** For point tests, use testing map with the following sequence:
     * 1 safe row
     * 1 bike lane
     * 1 taxi lane
     * 1 truck lane
     * 1 train lane
     * 1 river row
     */

    // The player should gain 1 point for jumping off the starting safe tile
    @Test(timeout = TIMEOUT)
    public void testSafeTilePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        waitForJump(game, player, MovementDirection.UP);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE);
    }

    // the player should gain 1 + BIKE_LANE_BONUS_POINTS additional points
    // for jumping over the bike lane
    @Test(timeout = TIMEOUT)
    public void testBikeLanePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 2;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * totalJumps
                + BIKE_LANE_BONUS_POINTS);
    }

    // The player should gain 1 + TAXI_LANE_BONUS_POINTS additional points
    // for jumping over the taxi lane
    @Test(timeout = TIMEOUT)
    public void testTaxiLanePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 3;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * totalJumps
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS);
    }

    // the player should gain 1 + TRUCK_LANE_BONUS_POINTS additional points
    //     for jumping over the truck lane
    @Test(timeout = TIMEOUT)
    public void testTruckLanePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 4;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * totalJumps
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS + TRUCK_LANE_BONUS_POINTS);
    }

    // the player should gain 1 + TRAIN_LANE_BONUS_POINTS additional points
    //     for jumping over the train lane
    @Test(timeout = TIMEOUT)
    public void testTrainLanePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 5;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * totalJumps
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS + TRUCK_LANE_BONUS_POINTS
                + TRAIN_LANE_BONUS_POINTS);
    }

    // the player should gain 1 + WATER_BONUS_POINTS additional points
    //     for jumping over the water tile lane
    @Test(timeout = TIMEOUT)
    public void testWaterTilePoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        int totalJumps = 6;
        waitForJumps(game, player, MovementDirection.UP, totalJumps);

        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * totalJumps
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS + TRUCK_LANE_BONUS_POINTS
                + TRAIN_LANE_BONUS_POINTS + WATER_BONUS_POINTS);
    }

    // check that jumping sideways does not change point values
    @Test(timeout = TIMEOUT)
    public void testNoSidewaysMovementPoints() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        waitForJumps(game, player, MovementDirection.LEFT, 3);

        // points should not increase (sideways movement)
        assertEquals(player.getPoints(), 0);

        waitForJumps(game, player, MovementDirection.RIGHT, 7);

        // points should not increase (sideways movement)
        assertEquals(player.getPoints(), 0);

        waitForJump(game, player, MovementDirection.UP);

        // points should by 1, since we hopped ahead from the safe tile
        assertEquals(player.getPoints(), 1);

        waitForJumps(game, player, MovementDirection.LEFT, 6);

        // points should not increase (sideways movement)
        assertEquals(player.getPoints(), 1);
    }

    // check that we can not increase points by repeating forwards/backwards progress
    @Test(timeout = TIMEOUT)
    public void testNoRepeatedPointGain() {
        Game game = createGameWithMap(createScoreTestingMap());
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        assertEquals(player.getPoints(), 0);

        waitForJumps(game, player, MovementDirection.UP, 3);

        // we went forwards, so now our points should increase
        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * 3
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS);

        waitForJumps(game, player, MovementDirection.DOWN, 5);

        // points should not increase
        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * 3
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS);

        waitForJumps(game, player, MovementDirection.UP, 5);

        // points should not increase (pre-trodden ground)
        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * 3
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS);

        waitForJump(game, player, MovementDirection.UP);

        // we went forwards one more, so now our points should increase again
        assertEquals(player.getPoints(), BASE_ADVANCEMENT_POINT_VALUE * 4
                + BIKE_LANE_BONUS_POINTS + TAXI_LANE_BONUS_POINTS + TRUCK_LANE_BONUS_POINTS);
    }

    /* Sprint 1/2 Tests ========================================================================= */

    // Player should not be allowed to move off-screen.
    // we check this by placing the player in the top-left corner
    //     and trying to more up or left (ensuring no position change)
    // we then place the player in the bottom-right corner
    //     and trying to more down or right (ensuring no position change)
    @Test(timeout = TIMEOUT)
    public void testMovementBounds() {
        Game game = createDefaultInitializedGame();
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        WorldMap worldMap = game.getMap();

        // test top and left bounds
        player.setPosition(new Vector2F(0, 0));

        assertVectorEquals(player.getPosition(), new Vector2F(0, 0));

        waitForJump(game, player, MovementDirection.UP);

        assertVectorEquals(player.getPosition(), new Vector2F(0, 0));

        waitForJump(game, player, MovementDirection.LEFT);

        assertVectorEquals(player.getPosition(), new Vector2F(0, 0));

        // test bottom and right bounds
        int maxTileX = worldMap.getTileWidth() - 1;
        int maxTileY = worldMap.getTileHeight() - 1;

        player.setPosition(new Vector2F(maxTileX, maxTileY));

        assertVectorEquals(player.getPosition(), new Vector2F(maxTileX, maxTileY));

        waitForJump(game, player, MovementDirection.DOWN);

        assertVectorEquals(player.getPosition(), new Vector2F(maxTileX, maxTileY));

        waitForJump(game, player, MovementDirection.RIGHT);

        assertVectorEquals(player.getPosition(), new Vector2F(maxTileX, maxTileY));
    }

    // Movement up should decrement Y coord by 1.
    @Test(timeout = TIMEOUT)
    public void testMovementUp() {
        Game game = createDefaultInitializedGame();
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        Vector2F initialPlayerPosition = player.getPosition();

        waitForJump(game, player, MovementDirection.UP);

        Vector2F finalPlayerPosition = player.getPosition();

        assertVectorEquals(
                new Vector2F(initialPlayerPosition.getX(), initialPlayerPosition.getY() - 1),
                finalPlayerPosition
        );
    }

    // Movement down should increment Y coord by 1.
    @Test(timeout = TIMEOUT)
    public void testMovementDown() {
        Game game = createDefaultInitializedGame();
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        Vector2F initialPlayerPosition = player.getPosition();

        waitForJump(game, player, MovementDirection.DOWN);

        Vector2F finalPlayerPosition = player.getPosition();

        assertVectorEquals(
                new Vector2F(initialPlayerPosition.getX(), initialPlayerPosition.getY() + 1),
                finalPlayerPosition
        );
    }

    // Movement to the right should decrement X coord by 1.
    @Test(timeout = TIMEOUT)
    public void testMovementLeft() {
        Game game = createDefaultInitializedGame();
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        Vector2F initialPlayerPosition = player.getPosition();

        waitForJump(game, player, MovementDirection.LEFT);

        Vector2F finalPlayerPosition = player.getPosition();

        assertVectorEquals(
                new Vector2F(initialPlayerPosition.getX() - 1, initialPlayerPosition.getY()),
                finalPlayerPosition
        );
    }

    // Movement to the right should increment X coord by 1.
    @Test(timeout = TIMEOUT)
    public void testMovementRight() {
        Game game = createDefaultInitializedGame();
        Player player = game.getPlayer();
        player.setInvincible(true); // make sure player doesn't die for test

        Vector2F initialPlayerPosition = player.getPosition();

        waitForJump(game, player, MovementDirection.RIGHT);

        Vector2F finalPlayerPosition = player.getPosition();

        assertVectorEquals(
                new Vector2F(initialPlayerPosition.getX() + 1, initialPlayerPosition.getY()),
                finalPlayerPosition
        );
    }

    // Starting lives on Easy Mode should be 5.
    @Test(timeout = TIMEOUT)
    public void testStartingLivesEasy() {
        Game game = createInitializedGameWithDifficulty(Difficulty.EASY);

        assertEquals(Difficulty.EASY.getStartingHearts(), 5);
        assertEquals(game.getPlayer().getInitialLives(),
                Difficulty.EASY.getStartingHearts());
        assertEquals(game.getPlayer().getLivesRemaining(),
                Difficulty.EASY.getStartingHearts());
    }

    // Starting lives on Medium Mode should be 3.
    @Test(timeout = TIMEOUT)
    public void testStartingLivesMedium() {
        Game game = createInitializedGameWithDifficulty(Difficulty.NORMAL);

        assertEquals(Difficulty.NORMAL.getStartingHearts(), 3);
        assertEquals(game.getPlayer().getInitialLives(),
                Difficulty.NORMAL.getStartingHearts());
        assertEquals(game.getPlayer().getLivesRemaining(),
                Difficulty.NORMAL.getStartingHearts());
    }

    // Starting lives on Hard Mode should be 1.
    @Test(timeout = TIMEOUT)
    public void testStartingLivesHard() {
        Game game = createInitializedGameWithDifficulty(Difficulty.HARD);

        assertEquals(Difficulty.HARD.getStartingHearts(), 1);
        assertEquals(game.getPlayer().getInitialLives(),
                Difficulty.HARD.getStartingHearts());
        assertEquals(game.getPlayer().getLivesRemaining(),
                Difficulty.HARD.getStartingHearts());
    }

    private String[] generateValidNames(int numNames) {
        Random rand = new Random();
        String[] names = new String[numNames];
        for (int nameI = 0; nameI < numNames; nameI++) {
            String name = "";
            int length = rand.nextInt(32) + 1;
            for (int charI = 0; charI < length; charI++) {
                name += (char) (rand.nextInt(94) + '!');
            }
            names[nameI] = name;
        }
        return names;
    }

    private void testNames(String[] invalidNames, String invalidMessage) {
        for (String name : invalidNames) {
            System.out.println("Testing invalid name: " + name);
            assertEquals(invalidMessage, generateNameValidationText(name));
        }
        for (String name : generateValidNames(100)) {
            System.out.println("Testing valid name: " + name);
            assertEquals("", generateNameValidationText(name));
        }
    }

    // Name should be non-null.
    @Test(timeout = TIMEOUT)
    public void testNameNull() {
        String[] invalidNames = {null};
        testNames(invalidNames, "Name must not be null");
    }

    // Name should be at least 1 character in length.
    @Test(timeout = TIMEOUT)
    public void testNameEmpty() {
        String[] invalidNames = {"", new String()};
        testNames(invalidNames, "Name must not be empty");
    }

    // Name should be 32 or fewer characters in length.
    @Test(timeout = TIMEOUT)
    public void testNameLength() {
        String[] invalidNames = {
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "b                                          b",
        };
        testNames(invalidNames, "Name must be 32 or fewer characters");
    }

    // Name should not be all whitespace.
    @Test(timeout = TIMEOUT)
    public void testNameWhitespace() {
        String[] invalidNames = {" ", "  ", "   ", "    ", "     "};
        testNames(invalidNames, "Name cannot just be whitespace");
    }

    /* Inner class helpers ====================================================================== */
    private abstract static class RunnableTestChecker implements Runnable {
        public abstract void checkSuccess();
    }
}
