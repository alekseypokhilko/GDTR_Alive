package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.api.GameMode;

public class Player {
    private final Engine engine;

    private int index;
    private TrackRecord trackRecord;
    private final Runnable restartMethod;

    public Player(Engine engine, Runnable restartMethod) {
        this.engine = engine;
        this.restartMethod = restartMethod;
    }

    public void replay(GameMode mode) {
        if (GameMode.REPLAY != mode) {
            return;
        }
        if (trackRecord != null && index < trackRecord.getStates().size()) {
            EngineStateRecord state = trackRecord.getStates().get(index);
            state.replay = true;
            state.track = trackRecord.getTrack();
            state.perspectiveEnabled = true;
            state.setLeague(trackRecord.getLeague());
            engine.setReplayState(state);
            engine.setReplayMode(true);
            engine.timerTime = state.t == null ? 0 : state.t;
            index++;
        } else {
            index = 0;
            Utils.waitRestart(1000L, 1500L);
            if (restartMethod != null) {
                restartMethod.run();
            }
        }
    }

    public void reset() {
        index = 0;
        engine.setReplayMode(false);
    }

    public void setTrackRecord(TrackRecord trackRecord) {
        this.trackRecord = trackRecord;
    }
}
