package com.kessoku.bocchifrog.rendering;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.kessoku.bocchifrog.GameView;

import java.util.TimerTask;

public class RenderTask extends TimerTask {

    public RenderTask(SurfaceHolder surfaceHolder, GameView view) {
        this.surfaceHolder = surfaceHolder;
        this.view = view;
    }

    private final SurfaceHolder surfaceHolder;
    private final GameView view;
    private Canvas canvas = null;

    @Override
    public void run() {
        try {
            // lock canvas
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                // update and draw frame
                view.draw(canvas);
                view.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                try {
                    // unlock canvas
                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
