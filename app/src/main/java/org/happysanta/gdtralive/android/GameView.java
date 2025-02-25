package org.happysanta.gdtralive.android;

import android.graphics.Canvas;
import android.view.View;

import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.GdView;

public class GameView extends View {

    public GdView gdView;
    private final AndroidCanvas canvas;
    private final ModManager modManager;

    //timer
    public int width;
    public int height;
    public int height1;
    public boolean m_KZ;
    public long m_rJ;
    int m_uI;
    private boolean m_AZ;

    public GameView(GDActivity micro, ModManager modManager) {
        super(micro);
        this.modManager = modManager;
        canvas = new AndroidCanvas(modManager);
        m_uI = 0;
        m_AZ = true;
        m_rJ = -1L;
        invalidate();
        m_KZ = false;
        adjustDimensions();
    }

    public void adjustDimensions(boolean flag) {
        m_AZ = flag;
        adjustDimensions();
    }

    public void adjustDimensions() {
        width = getScaledWidth();
        height1 = height = getScaledHeight();
        if (m_KZ && m_AZ)
            height -= 80;
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

    // Draw boot logos and "something else"
    @Override
    public void onDraw(Canvas g) {
        g.save();
        float density = modManager.getGameTheme().getDensity();
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
        return Math.round(getWidth() / modManager.getGameTheme().getDensity());
    }

    public int getScaledHeight() {
        return Math.round(getHeight() / modManager.getGameTheme().getDensity());
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec), height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
