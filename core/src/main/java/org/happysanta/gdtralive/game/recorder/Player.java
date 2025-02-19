package org.happysanta.gdtralive.game.recorder;

import org.happysanta.gdtralive.game.engine.Engine;

public class Player {
    private final Engine engine;

    private int index;
    private boolean replayMode = false;
    private TrackRecord trackRecord;
    private Runnable restartMethod;

    public Player(Engine engine) {
        this.engine = engine;
    }

    public void replay() {
        if (!replayMode) {
            return;
        }
        if (trackRecord != null && index < trackRecord.getStates().size()) {
            EngineStateRecord state = trackRecord.getStates().get(index);
            state.replay = true;
            state.track = trackRecord.getTrack();
            state.perspectiveEnabled = true;
            engine.setReplayState(state);
            engine.setReplayMode(true);
            engine.timerTime = state.t;
            index++;
        } else {
            index = 0;
            waitRestart(1000L, 1500L);
            if (restartMethod != null) {
                restartMethod.run();
            }
        }
    }

    public void setRestartMethod(Runnable restartMethod) {
        this.restartMethod = restartMethod;
    }

    public void reset() {
        index = 0;
        replayMode = false;
        engine.setReplayMode(false);
    }

    public void waitRestart(long delayedRestartAtTime, long currentTimeMillis) {
        try {
            long l2 = 1000L;
            if (delayedRestartAtTime > 0L)
                l2 = Math.min(delayedRestartAtTime - currentTimeMillis, 1000L);
            if (l2 > 0L)
                Thread.sleep(l2);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean isReplayMode() {
        return replayMode;
    }

    public void setReplayMode(boolean replayMode) {
        this.replayMode = replayMode; //todo GameMode.REPLAY
    }

    public void setTrackRecord(TrackRecord trackRecord) {
        this.trackRecord = trackRecord;
    }
}
