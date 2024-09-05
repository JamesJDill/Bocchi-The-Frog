package com.kessoku.bocchifrog.particles;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.kessoku.bocchifrog.Game;

public abstract class ParticleEffect {
    public abstract void begin(@NonNull ParticleSystem particleSystem);

    public void begin() {
        begin(Game.getParticleSystem());
    }

    protected Color createColorF(float r, float g, float b, float a) {
        if (Game.isDynamicParticleColouringDisabled()) {
            return new Color();
        }
        return Color.valueOf(r, g, b, a);
    }

    protected int argbFromColor(Color color) {
        if (Game.isDynamicParticleColouringDisabled()) {
            return 0;
        }
        return color.toArgb();
    }
}
