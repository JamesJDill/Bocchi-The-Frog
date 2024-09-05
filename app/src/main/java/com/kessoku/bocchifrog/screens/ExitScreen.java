package com.kessoku.bocchifrog.screens;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kessoku.bocchifrog.Game;
import com.kessoku.bocchifrog.GameState;
import com.kessoku.bocchifrog.MainActivity;
import com.kessoku.bocchifrog.ui.Button;
import com.kessoku.bocchifrog.ui.ButtonText;

import org.jetbrains.annotations.NotNull;

public class ExitScreen extends Screen {

    @Override
    protected void initializeButtons(@NotNull Canvas canvas) {
        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        Button exitButton = new Button(
                Button.toRect(
                        canvasWidth / 2,
                        canvasHeight * 9 / 20,
                        canvasWidth / 3,
                        canvasWidth / 3
                ),
                new ButtonText("Yes"),
                MainActivity::exit
        );

        Button cancelButton = new Button(
                Button.toRect(
                        canvasWidth / 2,
                        canvasHeight * 3 / 5,
                        canvasWidth / 3,
                        canvasWidth / 3
                ),
                new ButtonText("Cancel"),
                () -> {
                    boolean lost = Game.getState() == GameState.LOSE_EXIT;
                    Game.setState(lost ? GameState.LOSE : GameState.WIN);
                }
        );

        buttons.add(exitButton);
        buttons.add(cancelButton);
    }

    @Override
    protected void drawBackground(@NotNull Canvas canvas) {
        canvas.drawARGB(150, 0, 0, 0);
        Paint popupPaint = new Paint();
        popupPaint.setStyle(Paint.Style.FILL);
        popupPaint.setColor(Color.argb(230, 255, 255, 255));
        canvas.drawRoundRect(
                canvas.getWidth() * 0.05f,
                canvas.getHeight() * 0.27f,
                canvas.getWidth() * 0.95f,
                canvas.getHeight() * 0.69f,
                50f,
                50f,
                popupPaint
        );
    }

    @Override
    protected void drawUI(@NotNull Canvas canvas) {
        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        String loseText = "Are you SURE you want to exit?";
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60f);

        canvas.drawText(
                loseText,
                canvasWidth / 2f - textPaint.measureText(loseText) / 2f,
                canvasHeight / 3f,
                textPaint
        );
    }

}
