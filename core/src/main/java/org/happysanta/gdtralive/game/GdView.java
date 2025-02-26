package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.LoadingState;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.model.MessageElement;
import org.happysanta.gdtralive.game.api.model.ViewState;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.api.external.GdCanvas;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;

import java.util.Timer;

public class GdView {

    private final Engine engine;
    private final FrameRender frameRender;
    private LoadingState loadingState;

    public boolean drawTimer;
    private int cameraX;
    private int cameraY;

    private boolean infoMessageNeedDeleting;
    private String infoMessage;
    private int infoMessageIndex;
    private Timer timer;

    public GdView(FrameRender frameRender, Engine engine, int width, int height) {
        this.frameRender = frameRender;
        infoMessageNeedDeleting = false;
        drawTimer = true;
        loadingState = LoadingState.SPLASH1;

        infoMessage = null;
        infoMessageIndex = 0;
        timer = new Timer();
        cameraX = 0;
        this.cameraY = height;
        this.engine = engine;
        this.engine.calculateDelta(Math.min(width, height));
    }

    public void setDrawTimer(boolean drawTimer) {
        this.drawTimer = drawTimer;
    }

    public void setLoadingState(LoadingState state) {
        loadingState = state;
    }

    public synchronized void showInfoMessage(String message, int time) {
        infoMessageNeedDeleting = false;
        infoMessageIndex++;
        infoMessage = message;
        if (timer != null) {
            timer.schedule(new MessageElement(() -> removeInfoMessage(infoMessageIndex)), time);
        }
    }

    public void removeInfoMessage(int index) {
        if (infoMessageIndex == index)
            infoMessageNeedDeleting = true;
    }

    public void drawGame(GdCanvas canvas, int width, int height) {
        if (engine == null) {
            return;
        }
        frameRender.setCanvas(canvas);
        if (loadingState == LoadingState.SPLASH1) {
            frameRender.drawLogo(Sprite.CODEBREW_LOGO, getViewState(width, height));
            return;
        } else if (loadingState == LoadingState.SPLASH2) {
            frameRender.drawLogo(Sprite.GD_LOGO, getViewState(width, height));
            return;
        }

        engine.updateElementPosition();
        double yOffset = height > width ? 2 : 1.3;
        moveTrack(-engine.currentX_MAYBE() + width / 2,
                (int) (engine.currentY_MAYBE() + height / yOffset), width);

        EngineStateRecord engineState = engine.getStateReference();
        ViewState viewState = getViewState(width, height);
        frameRender.drawFrame(viewState, engineState);
        if (engine.getRespawn() != null) {
            frameRender.drawRespawnBike(viewState, engine.getRespawn());
        }
        if (drawTimer) {
            frameRender.drawTimer(engine.timerTime, viewState);
        }
        if (infoMessage != null) {
            frameRender.drawInfoMessage(infoMessage, viewState);
            if (infoMessageNeedDeleting) {
                infoMessage = null;
                infoMessageNeedDeleting = false;
            }
        }
        frameRender.drawProgress(engine.getProgress(), viewState);
    }

    public void moveTrack(int cameraX, int cameraY, int width) {
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        engine.moveTrack(-cameraX, -cameraX + width);
    }

    private ViewState getViewState(int scaledWidth, int scaledHeight) {
        ViewState state = new ViewState();
        state.offsetX = cameraX;
        state.offsetY = cameraY;
        state.width = scaledWidth;
        state.height = scaledHeight;
        state.zoom = 3.5;
        state.drawBiker = engine.drawBiker ? 1 : 0;
        state.drawBike = engine.drawBike ? 1 : 0;
        state.drawBiker2 = engine.crashedInAir_MAYBE ? 1 : 0;
        return state;
    }

    public synchronized void destroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
