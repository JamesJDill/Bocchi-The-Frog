package com.kessoku.bocchifrog.entities.floaters;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.player.Player;
import com.kessoku.bocchifrog.terrain.WorldMap;

public class TurtleChain extends Floater {
    private static final float MAX_TIME_TILL_SUBMERSION = 1.75f;
    private static final float SUBMERSION_FIRST_FRAME_START = 0.2f;
    private static final float SUBMERSION_SECOND_FRAME_START = 0.1f;
    private static final float TIME_PER_SUBMERSION = 0.7f;
    private static final float EXHUMATION_TIME = 0.2f;
    private static final float EXHUME_FIRST_FRAME_END = MAX_TIME_TILL_SUBMERSION + 0.1f;
    private static final float EXHUME_SECOND_FRAME_END = MAX_TIME_TILL_SUBMERSION;

    private float timeTillSubmersion = MAX_TIME_TILL_SUBMERSION;

    @Override
    public void update(WorldMap map, float deltaTime, Player player) {
        super.update(map, deltaTime, player);

        timeTillSubmersion -= deltaTime;
        if (timeTillSubmersion <= -TIME_PER_SUBMERSION) {
            timeTillSubmersion = MAX_TIME_TILL_SUBMERSION + EXHUMATION_TIME;
        }
    }

    @Override
    protected boolean isSurfaced() {
        return !isSubmerged();
    }

    private boolean isSubmerged() {
        return timeTillSubmersion <= 0;
    }

    @Override
    protected int findSpriteId() {
        if (isSubmerged()) {
            return findSubmergedSpriteId();
        }

        if (timeTillSubmersion <= SUBMERSION_SECOND_FRAME_START
            || timeTillSubmersion > EXHUME_FIRST_FRAME_END) {
            return findSecondSubmergingSpriteId();
        }

        if (timeTillSubmersion <= SUBMERSION_FIRST_FRAME_START
            || timeTillSubmersion > EXHUME_SECOND_FRAME_END) {
            return findFirstSubmergingSpriteId();
        }

        return findFloatingSpriteId();
    }

    private int findFloatingSpriteId() {
        return facingRight() ? R.drawable.turt_chain_right : R.drawable.turt_chain_left;
    }

    private int findFirstSubmergingSpriteId() {
        return facingRight()
                ? R.drawable.turt_chain_submerging_1_right
                : R.drawable.turt_chain_submerging_1_left;
    }

    private int findSecondSubmergingSpriteId() {
        return facingRight()
                ? R.drawable.turt_chain_submerging_2_right
                : R.drawable.turt_chain_submerging_2_left;
    }

    private int findSubmergedSpriteId() {
        return R.drawable.turt_chain_submerged;
    }

    @Override
    protected float getSpeed() {
        return 2.0f;
    }

    @Override
    protected float getLength() {
        return 3;
    }

    @Override
    public float getSpawningPeriod() {
        return 2.5f;
    }
}
