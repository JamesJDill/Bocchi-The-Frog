package com.kessoku.bocchifrog.screens;

import android.graphics.Canvas;
import android.view.View.OnTouchListener;

import com.kessoku.bocchifrog.ui.Button;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen {

    private boolean initialized = false;
    private OnTouchListener listener;

    protected List<Button> buttons = new ArrayList<>();

    protected abstract void initializeButtons(@NotNull Canvas canvas);

    private void initializeListener() {
        listener = (view, motionEvent) -> {
            for (Button button : buttons) {
                if (button.update(motionEvent)) {
                    view.performClick();
                    return true;
                }
            }
            return false;
        };
    }

    protected abstract void drawBackground(Canvas canvas);
    protected abstract void drawUI(Canvas canvas);

    private void drawButtons(Canvas canvas) {
        for (Button button : buttons) {
            button.draw(canvas);
        }
    }

    public void draw(@NotNull Canvas canvas) {
        if (!initialized) {
            initializeButtons(canvas);
            initializeListener();
            initialized = true;
        }

        drawBackground(canvas);
        drawUI(canvas);
        drawButtons(canvas);
    }

    public OnTouchListener getListener() {
        return listener;
    }
}
