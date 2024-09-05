package com.kessoku.bocchifrog.screens;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kessoku.bocchifrog.Game;
import com.kessoku.bocchifrog.GameState;

import org.jetbrains.annotations.NotNull;

public class WinScreen extends RestartQuitScreen {
    private int winTextSize = 140;
    private int winTextDeltaSize = 10;

    @Override
    protected void drawUI(@NotNull Canvas canvas) {

        updateWinTextSize();

        String winText = "POGGERS!!!";
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(winTextSize);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                winText,
                canvas.getWidth() / 2f,
                canvas.getHeight() / 4f,
                textPaint
        );

        drawScore(canvas);
    }

    private void updateWinTextSize() {
        if (winTextSize > 160 || winTextSize < 140) {
            winTextDeltaSize *= -1;
        }
        winTextSize += winTextDeltaSize;
    }

    @Override
    protected void onQuitClick() {
        Game.setState(GameState.WIN_EXIT);
    }

}
