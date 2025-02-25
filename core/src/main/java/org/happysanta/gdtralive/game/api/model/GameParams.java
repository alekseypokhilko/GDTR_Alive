package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.GameMode;

public class GameParams {
    private final GameMode mode;
    private final TrackParams trackParams;
    private final int league;

    private TrackRecord trackRecord;
    private int level;
    private int track;

    private GameParams(GameMode mode, TrackParams trackParams, int league, int level, int track) {
        this.mode = mode;
        this.trackParams = trackParams;
        this.league = league;
        this.level = level;
        this.track = track;
    }

    private GameParams(GameMode mode, TrackParams trackParams) {
        this.mode = mode;
        this.league = trackParams.getLeague();
        this.trackParams = trackParams;
    }

    private GameParams(TrackRecord trackRecord) {
        this.trackRecord = trackRecord;
        this.mode = GameMode.REPLAY;
        this.league = trackRecord.getLeague();
        this.trackParams = trackRecord.getTrack();
    }

    public static GameParams of(GameMode mode, TrackParams trackParams, int league, int level, int track) {
        return new GameParams(mode, trackParams, league, level, track);
    }

    public static GameParams of(GameMode mode, TrackParams track) {
        return new GameParams(mode, track);
    }

    public static GameParams of(TrackRecord trackRecord) {
        return new GameParams(trackRecord);
    }

    public GameMode getMode() {
        return mode;
    }

    public TrackParams getTrackParams() {
        return trackParams;
    }

    public TrackRecord getTrackRecord() {
        return trackRecord;
    }

    public int getLevel() {
        return level;
    }

    public int getTrack() {
        return track;
    }

    public int getLeague() {
        return league;
    }
}
