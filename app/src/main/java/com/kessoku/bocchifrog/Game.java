package com.kessoku.bocchifrog;

import android.graphics.Canvas;

import com.kessoku.bocchifrog.particles.ParticleSystem;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.player.PlayerConfig;
import com.kessoku.bocchifrog.rendering.Camera;
import com.kessoku.bocchifrog.screens.WinScreen;
import com.kessoku.bocchifrog.terrain.WorldMap;
import com.kessoku.bocchifrog.screens.ExitScreen;
import com.kessoku.bocchifrog.screens.LoseScreen;
import com.kessoku.bocchifrog.screens.Screen;

import org.jetbrains.annotations.NotNull;

public class Game {

    private static Game instance;
    private static final int EXPECTED_FPS = 60;
    public static final float EXPECTED_FRAME_TIME = 1.0f / EXPECTED_FPS;
    private static final float INITIAL_UPDATE_PREPARATION_SECONDS = 15.0f;
    private static GameState state;

    private WorldMap map;
    private Player player;
    private Camera camera;

    private LoseScreen loseScreen;
    private WinScreen winScreen;
    private ExitScreen exitScreen;

    private static final ParticleSystem PARTICLE_SYSTEM = new ParticleSystem();
    private static boolean dynamicParticleColouringEnabled = true;

    public void initialize(PlayerConfig playerConfig, WorldMap chosenMap) {
        instance = this;

        // create game objects
        state = GameState.PLAY;
        map = chosenMap;
        player = new Player(playerConfig, WorldMap.PLAYER_SPAWN_X, WorldMap.PLAYER_SPAWN_Y);

        updateInstantlyFor(INITIAL_UPDATE_PREPARATION_SECONDS);

        camera = new Camera(map);
        camera.setAsMain();

        loseScreen = new LoseScreen();
        winScreen = new WinScreen();
        exitScreen = new ExitScreen();
    }

    public void initialize(PlayerConfig playerConfig) {
        initialize(playerConfig, WorldMap.generateGameMap());
    }
  
    public void updateInstantlyFor(float seconds, float delta) {
        for (float time = 0.0f; time < seconds; time += delta) {
            update(delta);
        }
    }

    public void updateInstantlyFor(float seconds) {
        updateInstantlyFor(seconds, EXPECTED_FRAME_TIME);
    }

    public void update(float deltaTime) {
        map.updateWorld(deltaTime, player);

        // update player after world!
        if (state == GameState.PLAY) {
            player.update(map, deltaTime);
        }

        PARTICLE_SYSTEM.update(deltaTime);

        if (camera != null) {
            Vector2F cameraTrackPosition =
                player.isRespawning()
                    ? PARTICLE_SYSTEM.getLastAverageParticleLocation()
                    : player.getPosition();

            camera.update(cameraTrackPosition, deltaTime);
        }
    }

    public void draw(@NotNull Canvas canvas) {
        // clear the background to white
        canvas.drawARGB(255, 255, 255, 255);

        // draw game objects
        map.drawBackground(canvas);

        PARTICLE_SYSTEM.draw(canvas);

        map.drawMidGround(canvas);

        player.draw(canvas);

        map.drawForeground(canvas);

        drawUI(canvas);

        if (state == GameState.LOSE || state == GameState.LOSE_EXIT) {
            loseScreen.draw(canvas);
        } else if (state == GameState.WIN || state == GameState.WIN_EXIT) {
            winScreen.draw(canvas);
        }
        if (state == GameState.LOSE_EXIT || state == GameState.WIN_EXIT) {
            exitScreen.draw(canvas);
        }
    }

    public void drawUI(@NotNull Canvas canvas) {
        player.drawUI(canvas);
    }

    public WorldMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public Camera getCamera() {
        return camera;
    }

    public static ParticleSystem getParticleSystem() {
        return PARTICLE_SYSTEM;
    }

    public static GameState getState() {
        return state;
    }

    public static void setState(GameState state) {
        Game.state = state;
    }

    public Screen getLoseScreen() {
        return loseScreen;
    }

    public Screen getWinScreen() {
        return winScreen;
    }

    public Screen getExitScreen() {
        return exitScreen;
    }

    public static void lose() {
        state = GameState.LOSE;
        instance.loseScreen.setScore(instance.player.getPoints());
    }

    public static void win() {
        state = GameState.WIN;
        instance.winScreen.setScore(instance.player.getPoints());
    }

    public static void disableDynamicParticleColouring() {
        dynamicParticleColouringEnabled = false;
    }

    public static boolean isDynamicParticleColouringDisabled() {
        return !dynamicParticleColouringEnabled;
    }

    public boolean isGameOver() {
        return state == GameState.LOSE;
    }
}