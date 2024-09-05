package com.kessoku.bocchifrog.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Game;
import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.Vector2F;
import com.kessoku.bocchifrog.entities.DirectionalEntity;
import com.kessoku.bocchifrog.entities.floaters.Floater;
import com.kessoku.bocchifrog.particles.SplashParticleEffect;
import com.kessoku.bocchifrog.particles.VaporizeParticleEffect;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;
import com.kessoku.bocchifrog.terrain.TerrainTile;
import com.kessoku.bocchifrog.terrain.TerrainTileType;
import com.kessoku.bocchifrog.terrain.WorldMap;

public class Player {
    // player constants
    public static final float TOTAL_JUMP_TIME = 0.2f;
    private static final float TOTAL_RESPAWN_TIME = 1.5f;


    // player variables
    private final String name;
    private final PlayerCharacter character;
    private final Difficulty difficulty;

    private Bitmap sprite;
    private int points = 0;
    private int livesRemaining;
    private final int initialLives;
    private Vector2F position;
    private Vector2F velocity;
    private int furthestY;
    private float jumpTimeRemaining = 0.0f;
    private float respawnTimeRemaining = 0.0f;
    private boolean isInvincible = false;
    private MovementDirection queuedMovement = null;
    private boolean ridingFloater = false;

    public Player(PlayerConfig playerConfig, int x, int y) {
        name = playerConfig.getName();
        position = new Vector2F(x, y);
        velocity = new Vector2F(0, 0);

        character = playerConfig.getCharacter();
        difficulty = playerConfig.getDifficulty();
        sprite = loadSprite(character);

        initialLives = difficulty.getStartingHearts();
        livesRemaining = initialLives;
        furthestY = y + 1;
    }

    public void update(WorldMap map, float deltaTime) {
        if (isRespawning()) {
            respawnTimeRemaining -= deltaTime;
            return;
        }

        if (isJumping()) {
            jumpTimeRemaining -= deltaTime;

            // jump ended this frame
            if (!isJumping()) {
                landJump(map);
            }
        } else { // grounded
            // drown if you are in water but not on a floater :'(
            if (isOnWater(map) && !ridingFloater) {
                drown();
                return;
            }
        }

        // update position via velocity
        position = new Vector2F(
                position.getX() + velocity.getX() * deltaTime,
                position.getY() + velocity.getY() * deltaTime
        );

        // clamp position within bounds
        position = new Vector2F(
                Math.max(0, Math.min(map.getTileWidth() - 1, position.getX())),
                Math.max(0, Math.min(map.getTileHeight() - 1, position.getY()))
        );

        // post-update per-frame actions
        ridingFloater = false;
    }

    public void dieViaCrushing(Vector2F knockBackForce) {
        if (isInvincible() || isRespawning()) {
            return;
        }

        Vector2F particleBaseVelocity =
            knockBackForce
                .displacedBy(
                    velocity.scaledBy(-0.5f)
                );

        new VaporizeParticleEffect(sprite, position, particleBaseVelocity).begin();
        die();
    }

    public boolean isOnWater(WorldMap worldMap) {
        return isOnWaterAt(worldMap, position);
    }

    public boolean isOnWaterAt(WorldMap worldMap, Vector2F tilePos) {
        TerrainTileType tileTypeOn = worldMap
                .tileTypeAt((int) tilePos.getX(), (int) tilePos.getY());

        return tileTypeOn == TerrainTileType.WATER;

    }

    public boolean isOnGoal(WorldMap worldMap) {
        TerrainTileType tileTypeOn = worldMap
                .tileTypeAt((int) position.getX(), (int) position.getY());

        return tileTypeOn == TerrainTileType.GOAL;
    }

    public void drown() {
        if (isInvincible() || isRespawning()) {
            return;
        }

        Vector2F splashCenter = position.displacedBy(new Vector2F(0.5f, 0.5f));
        new SplashParticleEffect(splashCenter).begin();
        die();
    }

    public void die() {
        if (isInvincible() || isRespawning()) {
            return;
        }

        // reset variables
        setPosition(new Vector2F(WorldMap.PLAYER_SPAWN_X, WorldMap.PLAYER_SPAWN_Y));
        velocity = new Vector2F(0, 0);
        sprite = loadSprite(character);
        jumpTimeRemaining = 0.0f;

        // begin respawn
        respawnTimeRemaining = TOTAL_RESPAWN_TIME;

        // lose a life
        livesRemaining--;

        // lose half of current points (floored)
        points /= 2;

        // end game if lives reach 0
        if (livesRemaining <= 0) {
            Game.lose();
        }
    }

    public void inputJumpMovement(WorldMap map, MovementDirection direction) {
        if (isRespawning()) {
            return;
        }

        if (!isJumping()) {
            beginJump(map, direction);
        } else {
            queuedMovement = direction;
        }
    }

    private void beginJump(WorldMap map, MovementDirection direction) {
        queuedMovement = null;

        velocity = direction.calcVelocityForJump(TOTAL_JUMP_TIME);
        setCorrectiveJumpVelocityX(map, direction);
        jumpTimeRemaining = TOTAL_JUMP_TIME;
        sprite = loadSprite(character, direction);
    }

    private void setCorrectiveJumpVelocityX(WorldMap map, MovementDirection direction) {
        if (direction == MovementDirection.LEFT || direction == MovementDirection.RIGHT) {
            return;
        }

        Vector2F targetPos = position.displacedBy(direction.unitVector());
        Vector2F destSnapped = snapToGrid(targetPos);
        if (isOnWaterAt(map, destSnapped)) {
            Floater floater = findFloaterCollidingAtTarget(map, targetPos);
            if (floater != null) {
                float diffX = floater.nearestNodeXPositionAfter(targetPos, TOTAL_JUMP_TIME)
                        - position.getX();
                velocity.setX(diffX / TOTAL_JUMP_TIME);
            }
        } else { // corrects jumps from water-floaters to land
            float diffX = destSnapped.getX() - position.getX();
            velocity.setX(diffX / TOTAL_JUMP_TIME);
        }
    }

    public boolean hasFloaterAhead(WorldMap map) {
        return findFloaterCollidingAtTarget(
                map, position.displacedBy(MovementDirection.UP.unitVector())
        ) != null;
    }

    private Floater findFloaterCollidingAtTarget(WorldMap map, Vector2F targetPos) {
        for (DirectionalEntity entity : map.getEntities()) {
            if (entity instanceof Floater
                    && entity.collidesWithPlayerAt(
                            targetPos,
                    entity.calcVelocityX() * TOTAL_JUMP_TIME)
            ) {
                return (Floater) entity;
            }
        }
        return null;
    }

    private void landJump(WorldMap map) {
        snapYToGrid();
        if (!isOnWater(map)) {
            position = snapToGrid(position);
        }
        velocity = new Vector2F(0, 0);

        if (queuedMovement != null) {
            beginJump(map, queuedMovement);
        }

        // add points on jump landing
        updatePoints(map);
      
        if (isOnGoal(map)) {
            points += 50;
            Game.win();
        }
    }

    public boolean shouldRideFloater() {
        return !isJumping() || velocity.getY() == 0; // grounded or moving horizontally
    }

    public void rideFloater(WorldMap map, float displacement) {
        position.setX(position.getX() + displacement);

        float edgeHitKnockBackAmount = 3.0f;

        // die if dragged out of map by log
        if (position.getX() < 0) {
            dieViaCrushing(new Vector2F(edgeHitKnockBackAmount, 0));
            return;
        }

        if (position.getX() > map.getTileWidth() - 1) {
            dieViaCrushing(new Vector2F(-edgeHitKnockBackAmount, 0));
            return;
        }

        ridingFloater = true;
    }

    private boolean isJumping() {
        return jumpTimeRemaining > 0;
    }

    public boolean isRespawning() {
        return respawnTimeRemaining > 0;
    }

    private void snapYToGrid() {
        position.setY(snapToGrid(position).getY());
    }

    private static Vector2F snapToGrid(Vector2F p) {
        return new Vector2F(Math.round(p.getX()), Math.round(p.getY()));
    }

    private void updatePoints(WorldMap map) {
        if (position.getY() + 1 < furthestY) {
            furthestY = (int) position.getY() + 1;
            points++;
            TerrainTileType currTileType = map.tileTypeAt((int) position.getX(), furthestY);
            if (currTileType == TerrainTileType.WATER) {
                points += TerrainTile.WATER_BONUS;
            } else if (map.getLaneMap().containsKey(furthestY)) {
                points += map.getLaneMap().get(furthestY).getPointBonus();
            }
        }
    }

    private Bitmap loadSprite(PlayerCharacter character) {
        return loadSprite(character, MovementDirection.UP);
    }

    private Bitmap loadSprite(PlayerCharacter character, MovementDirection direction) {
        if (character == null) {
            throw new IllegalArgumentException("Can not load sprite when character is null!");
        }
        return BitmapManager.getById(character.getSpriteId(direction));
    }

    public void draw(@NonNull Canvas canvas) {
        if (isRespawning()) {
            return;
        }

        RectF destRect = RenderUtil.tileRenderRectF(position.getX(), position.getY());

        // draw shadow
        Bitmap shadowBitmap = BitmapManager.getById(R.drawable.shadow);
        canvas.drawBitmap(
                shadowBitmap, null, destRect, new Paint()
        );

        // draw sprite
        float maxJumpExpand = 64.0f;
        float jumpExpand =
                (float) Math.sin(
                        Math.min(1,
                            Math.max(0, (TOTAL_JUMP_TIME - jumpTimeRemaining) / TOTAL_JUMP_TIME))
                                * Math.PI
                ) * maxJumpExpand;

        RectF jumpExpandedDest = new RectF(
                destRect.left - jumpExpand,
                destRect.top - jumpExpand,
                destRect.right + jumpExpand,
                destRect.bottom + jumpExpand
        );

        canvas.drawBitmap(
            sprite, null, jumpExpandedDest, new Paint()
        );
    }

    public void drawUI(@NonNull Canvas canvas) {
        PlayerHUD.drawPlayerHUD(canvas, this);
    }

    public Vector2F getPosition() {
        return position;
    }

    public void setPosition(Vector2F position) {
        this.position = position;
    }

    public int getLivesRemaining() {
        return livesRemaining;
    }

    public int getInitialLives() {
        return initialLives;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }
}
