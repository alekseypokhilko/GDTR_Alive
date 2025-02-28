package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.GameMode;

public class GameParams {
    private final GameMode mode;
    private final TrackData trackData;
    private final int league;

    private TrackRecord trackRecord;
    private int level;
    private int track;

    private GameParams(GameMode mode, TrackData trackData, int league, int level, int track) {
        this.mode = mode;
        this.trackData = trackData;
        this.league = league;
        this.level = level;
        this.track = track;
    }

    private GameParams(GameMode mode, TrackData trackData) {
        this.mode = mode;
        this.league = trackData.getLeague();
        this.trackData = trackData;
    }

    private GameParams(TrackRecord trackRecord) {
        this.trackRecord = trackRecord;
        this.mode = GameMode.REPLAY;
        this.league = trackRecord.getLeague();
        this.trackData = trackRecord.getTrack();
    }

    public static GameParams of(GameMode mode, TrackData trackData, int league, int level, int track) {
        return new GameParams(mode, trackData, league, level, track);
    }

    public static GameParams of(GameMode mode, TrackData track) {
        return new GameParams(mode, track);
    }

    public static GameParams of(TrackRecord trackRecord) {
        return new GameParams(trackRecord);
    }

    public GameMode getMode() {
        return mode;
    }

    public TrackData getTrackParams() {
        return trackData;
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
