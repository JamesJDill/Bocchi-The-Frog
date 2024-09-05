package com.kessoku.bocchifrog;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnDirectionalTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnDirectionalTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }
    @Override
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public void onInputUp() { }
    public void onInputDown() { }
    public void onInputLeft() { }
    public void onInputRight() { }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final float SWIPE_DIRECTIONAL_THRESHOLD = 50;

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                               float velocityX, float velocityY) {

            float x1 = e1.getX();
            float y1 = e1.getY();

            float x2 = e2.getX();
            float y2 = e2.getY();

            float diffX = x2 - x1;
            float diffY = y2 - y1;

            double dist = Math.hypot(diffX, diffY);

            // if the swipe is not very significant, default to up
            if (dist < SWIPE_DIRECTIONAL_THRESHOLD) {
                onInputUp();
                return true;
            }

            if (Math.abs(diffX) > Math.abs(diffY)) {
                // horizontal swipe
                if (diffX > 0) {
                    onInputRight();
                } else {
                    onInputLeft();
                }
            } else {
                // vertical swipe
                if (diffY > 0) {
                    onInputDown();
                } else {
                    onInputUp();
                }
            }

            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            onInputUp();
            return true;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }
    }
}
