package com.kessoku.bocchifrog.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

public class ButtonText {

    private final String text;
    private final int size;
    private final int color;

    public ButtonText(String text) {
        this(text, 50, Color.WHITE);
    }

    public ButtonText(String text, int size, int color) {
        this.text = text;
        this.size = size;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public TextPaint getTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(size);
        textPaint.setColor(color);
        return textPaint;
    }

}
