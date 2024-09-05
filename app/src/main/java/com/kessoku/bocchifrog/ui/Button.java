package com.kessoku.bocchifrog.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kessoku.bocchifrog.R;
import com.kessoku.bocchifrog.rendering.BitmapManager;

public class Button extends Drawable {

    private static final float ACTIVE_SCALE = 0.9f;
    private static final Bitmap NORMAL_SPRITE = BitmapManager.getById(R.drawable.bocchi_upscale_up);
    private static final Bitmap ACTIVE_SPRITE = BitmapManager.getById(R.drawable.kita_upscale_up);

    private final ButtonText buttonText;
    private final Rect normalRect;
    private final Rect activeRect;
    private final Runnable onClick;

    private boolean isClicked = false;

    public Button(Rect drawRect, ButtonText buttonText, Runnable onClick) {

        this.normalRect = drawRect;
        this.activeRect = toRect(
                drawRect.centerX(),
                drawRect.centerY(),
                (int) (drawRect.width() * ACTIVE_SCALE),
                (int) (drawRect.height() * ACTIVE_SCALE)
        );

        this.buttonText = buttonText;
        this.onClick = onClick;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        String text = buttonText.getText();
        TextPaint paint = buttonText.getTextPaint();
        Bitmap sprite = isClicked ? ACTIVE_SPRITE : NORMAL_SPRITE;

        assert sprite != null;
        canvas.drawBitmap(sprite, null, isClicked ? activeRect : normalRect, new Paint());

        canvas.drawText(
                text,
                normalRect.centerX() - paint.measureText(text) / 2,
                normalRect.centerY() - (paint.descent() + paint.ascent()) / 2,
                paint
        );

    }

    @Override
    public void setAlpha(int i) { }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public static Rect toRect(int x, int y, int width, int height) {
        return new Rect(
                x - width / 2,
                y - height / 2,
                x + width / 2,
                y + height / 2
        );
    }

    public boolean update(MotionEvent event) {
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (normalRect.contains(mouseX, mouseY)) {
                isClicked = true;
                return true;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (isClicked && normalRect.contains(mouseX, mouseY)) {
                onClick.run();
                isClicked = false;
                return true;
            }
            isClicked = false;
        }
        return false;
    }

}
