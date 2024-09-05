package com.kessoku.bocchifrog.screens;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kessoku.bocchifrog.MainActivity;
import com.kessoku.bocchifrog.ui.Button;
import com.kessoku.bocchifrog.ui.ButtonText;

import org.jetbrains.annotations.NotNull;

public abstract class RestartQuitScreen extends Screen {

    private int score = 0;

    @Override
    protected void initializeButtons(@NotNull Canvas canvas) {
        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        Button restartButton = new Button(
                Button.toRect(
                        canvasWidth / 2,
                        canvasHeight / 2,
                        canvasWidth / 3,
                        canvasWidth / 3
                ),
                new ButtonText("Restart"),
                MainActivity::resetCharacterSelectScreen
        );

        Button quitButton = new Button(
                Button.toRect(
                        canvasWidth / 2,
                        canvasHeight * 2 / 3,
                        canvasWidth / 3,
                        canvasWidth / 3
                ),
                new ButtonText("Quit"),
                this::onQuitClick
        );

        buttons.add(restartButton);
        buttons.add(quitButton);
    }

    @Override
    protected void drawBackground(@NotNull Canvas canvas) {
        canvas.drawARGB(150, 0, 0, 0);
    }

    protected void drawScore(@NotNull Canvas canvas) {
        Paint paint = new Paint();
        String scoreText = "Score: " + score;
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                scoreText,
                canvas.getWidth() / 2f,
                canvas.getHeight() / 3f,
                paint
        );
    }

    protected abstract void onQuitClick();

    public void setScore(int score) {
        this.score = score;
    }

}
