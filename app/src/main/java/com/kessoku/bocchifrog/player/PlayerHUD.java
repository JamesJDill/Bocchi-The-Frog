package com.kessoku.bocchifrog.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.rendering.BitmapManager;
import com.kessoku.bocchifrog.rendering.RenderUtil;
import com.kessoku.bocchifrog.rendering.Rendering;

public class PlayerHUD {
    // player UI constants
    private static final float UI_TEXT_SIZE = 50.0f;
    private static final float UI_MARGIN = 10.0f;
    private static final float UI_SPACING = 10.0f;
    private static final float UI_HEART_SIZE = 128;

    public static void drawPlayerHUD(@NonNull Canvas canvas, Player player) {
        // name and points on top strip
        drawHudName(canvas, player.getName());
        drawHudPoints(canvas, player.getPoints());

        // mode and hearts on bottom strip
        drawHudMode(canvas, player.getDifficulty());
        drawHudHearts(canvas, player.getInitialLives(), player.getLivesRemaining());
    }

    private static Paint createTextPaint() {
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(UI_TEXT_SIZE);
        textPaint.setFakeBoldText(true);

        return textPaint;
    }

    private static void drawHudName(@NonNull Canvas canvas, String name) {
        canvas.drawText(
                "Name: " + name,
                UI_MARGIN, UI_TEXT_SIZE + UI_MARGIN,
                createTextPaint()
        );
    }

    private static void drawHudPoints(@NonNull Canvas canvas, int points) {
        canvas.drawText(
                "Points: " + points,
                UI_MARGIN, UI_TEXT_SIZE * 2 + UI_SPACING + UI_MARGIN,
                createTextPaint()
        );
    }

    private static void drawHudMode(@NonNull Canvas canvas, Difficulty mode) {
        canvas.drawText(
                "Mode: " + mode.name(),
                UI_MARGIN, Rendering.getViewHeight() - UI_TEXT_SIZE,
                createTextPaint()
        );
    }

    private static void drawHudHearts(@NonNull Canvas canvas,
                                      int initialLives, int livesRemaining) {
        for (int i = 0; i < initialLives; i++) {
            int heartSpriteId = (i < livesRemaining)
                    ? R.drawable.heart_alive
                    : R.drawable.heart_dead;
            Bitmap heartBitmap = BitmapManager.getById(heartSpriteId);

            RectF heartRect = RenderUtil.sizedRectF(
                    Rendering.getViewWidth()
                            - (UI_HEART_SIZE + UI_MARGIN + i * (UI_SPACING + UI_HEART_SIZE)),
                    Rendering.getViewHeight()
                            - (UI_HEART_SIZE + UI_MARGIN),
                    UI_HEART_SIZE,
                    UI_HEART_SIZE
            );

            canvas.drawBitmap(heartBitmap, null, heartRect, new Paint());
        }
    }
}
