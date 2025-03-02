package org.happysanta.gdtralive.android;

import android.graphics.Canvas;
import android.view.View;

import org.happysanta.gdtralive.game.GdView;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.external.GdGameView;

public class AGameView extends View implements GdGameView {

    public GdView gdView;
    private ACanvas canvas;
    private ModManager modManager;

    public int width;
    public int height;
    private int height1;

    public AGameView(GDActivity micro) {
        super(micro);
        invalidate();
        adjustDimensions();
    }

    public void setModManager(ModManager modManager) {
        this.modManager = modManager;
        this.canvas = new ACanvas(modManager);
    }

    public void adjustDimensions() {
        if (modManager == null) {
            return;
        }
        width = getScaledWidth();
        height1 = height = getScaledHeight();
        //postInvalidate();
    }

    public void setGdView(GdView gdView) {
        this.gdView = gdView;
    }

    public int getGdWidth() {
        return width;
    }

    public int getGdHeight() {
        return height;
    }

    public GdView getGdView() {
        return gdView;
    }

    // Draw boot logos and "something else"
    @Override
    public void onDraw(Canvas g) {
        g.save();
        float density = modManager.getGameDensity();
        g.scale(density, density);
        if (height1 != getHeight()) {
            adjustDimensions();
        }
        canvas.setCanvas(g);
        if (gdView != null) {
            gdView.drawGame(canvas, width, height);
        }
        g.restore();
        invalidate();
    }

    public int getScaledWidth() {
        return Math.round(getWidth() / modManager.getGameDensity());
    }

    public int getScaledHeight() {
        return Math.round(getHeight() / modManager.getGameDensity());
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec), height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
