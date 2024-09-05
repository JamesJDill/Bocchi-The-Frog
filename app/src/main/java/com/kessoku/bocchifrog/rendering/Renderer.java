package com.kessoku.bocchifrog.rendering;

import com.kessoku.bocchifrog.GameView;

import java.util.Timer;

public class Renderer {
    private final RenderTask task;
    private final Timer t;
    private final int fps;

    public Renderer(GameView view, int fps) {
        task = new RenderTask(view.getHolder(), view);
        t = new Timer();
        this.fps = fps;
    }

    public void beginRendering() {
        t.scheduleAtFixedRate(task, 0, (long) (1.0 / fps * 1000));
    }
}
