package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.LoadingState;
import org.happysanta.gdtralive.game.api.Platform;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.model.MessageElement;
import org.happysanta.gdtralive.game.api.model.ViewState;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.api.external.GdCanvas;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.util.Fmt;

import java.util.Map;
import java.util.Timer;

public class GdView {

    private final GdPlatform platform;
    private final Engine engine;
    private final Game game;
    private final GdStr str;
    private final FrameRender frameRender;
    private LoadingState loadingState;

    public boolean drawTimer;
    private int cameraX;
    private int cameraY;

    private int shiftX;
    private int shiftY;

    private boolean infoMessageNeedDeleting;
    private String infoMessage;
    private int infoMessageIndex;
    private Timer timer;

    public GdView(FrameRender frameRender, Engine engine, int width, int height,
                  GdPlatform platform, Game game, GdStr str) {
        this.platform = platform;
        this.frameRender = frameRender;
        this.game = game;
        this.str = str;
        infoMessageNeedDeleting = false;
        drawTimer = true;
        loadingState = LoadingState.SPLASH1;

        infoMessage = null;
        infoMessageIndex = 0;
        timer = new Timer();
        cameraX = 0;
        shiftX = 0;
        shiftY = 0;
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
        double yOffset = height > width ? 2 : (platform.getPlatform() == Platform.MOBILE ? 1.3 : 1.7);
        moveTrack(-engine.currentX_MAYBE() + width / 2 + shiftX,
                (int) (engine.currentY_MAYBE() + height / yOffset + shiftY), width);

        EngineStateRecord engineState = engine.getStateReference();
        ViewState viewState = getViewState(width, height);
        frameRender.drawFrame(viewState, engineState);
        if (engine.getRespawn() != null) {
            frameRender.drawRespawnBike(viewState, engine.getRespawn());
        }
        for (Map.Entry<String, EngineStateRecord> opponent : engine.getOpponents().entrySet()) {
            EngineStateRecord state = opponent.getValue();
            state.name = opponent.getKey();
            state.replay = true;
            frameRender.drawOpponent(viewState, state);
        }
        if (drawTimer) {
            frameRender.drawTimer(engine.timerTime, viewState);
            frameRender.drawAttemptCounter(viewState, Fmt.sp(str.s(S.ATTEMPT), game.attemptCount));
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

    public void shift(int shiftX, int shiftY) {
        this.shiftX += shiftX;
        this.shiftY += shiftY;
    }

    public void resetShift() {
        this.shiftX = 0;
        this.shiftY = 0;
    }
}
