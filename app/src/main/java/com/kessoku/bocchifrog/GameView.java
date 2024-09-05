package com.kessoku.bocchifrog;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.kessoku.bocchifrog.player.MovementDirection;
import com.kessoku.bocchifrog.player.PlayerConfig;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.Rendering;

public class GameView extends SurfaceView {
    private long lastUpdateTimeNanos;

    private final Game game;

    public GameView(Context context) {
        super(context);
        setupView(context);
        game = new Game();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
        game = new Game();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
        game = new Game();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
        game = new Game();
    }

    private void setupView(Context context) {
        // setup our starting time for the calculation of deltaTime
        lastUpdateTimeNanos = System.nanoTime();

        // initialize BitmapManager to allow Bitmap loading by id
        BitmapManager.useResources(getResources());

        // attach listener for in-game movement
        setOnTouchListener(new OnDirectionalTouchListener(context) {
            @Override
            public void onInputUp() {
                game.getPlayer().inputJumpMovement(game.getMap(), MovementDirection.UP);
            }

            @Override
            public void onInputDown() {
                game.getPlayer().inputJumpMovement(game.getMap(), MovementDirection.DOWN);
            }

            @Override
            public void onInputLeft() {
                game.getPlayer().inputJumpMovement(game.getMap(), MovementDirection.LEFT);
            }

            @Override
            public void onInputRight() {
                game.getPlayer().inputJumpMovement(game.getMap(), MovementDirection.RIGHT);
            }
        });
    }

    public void initializeGame(PlayerConfig playerConfig) {
        game.initialize(playerConfig);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // setup relative tile-sizing
        Rendering.setupForCanvasWidth(getWidth(), getHeight());

        game.draw(canvas);

        switch (Game.getState()) {

        case LOSE:
            setOnTouchListener(game.getLoseScreen().getListener());
            break;
        case WIN:
            setOnTouchListener(game.getWinScreen().getListener());
            break;
        case LOSE_EXIT:
        case WIN_EXIT:
            setOnTouchListener(game.getExitScreen().getListener());
            break;
        default:
            break;

        }
    }

    public void update() {
        long currentTimeNanos = System.nanoTime();
        long deltaTimeNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaTime = (float) ((double) deltaTimeNanos / 1_000_000_000.0);

        // update game objects
        game.update(deltaTime);

        // update timestamp for calculation of deltaTime
        lastUpdateTimeNanos = currentTimeNanos;
    }
}
