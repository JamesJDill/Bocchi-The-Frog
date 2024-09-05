package com.kessoku.bocchifrog.screens;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kessoku.bocchifrog.Game;
import com.kessoku.bocchifrog.GameState;

import org.jetbrains.annotations.NotNull;

public class LoseScreen extends RestartQuitScreen {

    @Override
    protected void drawUI(@NotNull Canvas canvas) {

        String loseText = "It's Ikuyover 😔";
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(120);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                loseText,
                canvas.getWidth() / 2f,
                canvas.getHeight() / 4f,
                textPaint
        );

        drawScore(canvas);
    }

    @Override
    protected void onQuitClick() {
        Game.setState(GameState.LOSE_EXIT);
    }

}
