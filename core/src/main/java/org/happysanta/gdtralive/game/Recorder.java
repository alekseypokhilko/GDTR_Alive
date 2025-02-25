package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdSettings;

import java.util.ArrayList;
import java.util.List;

public class Recorder {
    private final Engine engine;
    private final GdSettings settings;
    private final GdFileStorage fileStorage;

    private boolean capturing = true;
    private List<EngineStateRecord> states = new ArrayList<>();

    public Recorder(Engine engine, GdFileStorage fileStorage, GdSettings settings) {
        this.engine = engine;
        this.settings = settings;
        this.fileStorage = fileStorage;
    }

    public void captureState() {
        if (capturing) {
            EngineStateRecord state = engine.getState();
            states.add(state);
        }
    }

    public void startCapture() {
        capturing = true;
        initCapturing();
    }

    public void initCapturing() {
        capturing = true;
        engine.setReplayMode(false);
        states = new ArrayList<>();
    }

    public void stopCapture() {
        if (capturing) {
            capturing = false;
            engine.setReplayMode(false);
        }
    }

    public void saveCapture(long time) {
        stopCapture();

        try {
            TrackRecord trackRecord = new TrackRecord(engine.getTrackPhysic().getTrack(), time, engine.league, settings.getPlayerName());
            trackRecord.setStates(states);
            fileStorage.addRecord(trackRecord);
        } catch (Exception ignore) {
        }

        reset();
    }

    public void reset() {
        states = new ArrayList<>();
    }

    public boolean isCapturing() {
        return capturing;
    }

    public boolean isCapturingMode() {
        return capturing;
    }

    public void setCapturingMode(boolean capturingMode) {
        this.capturing = capturingMode;
    }
}
