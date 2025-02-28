package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackRecord implements Serializable {
    private final String trackGuid;
    private final String trackName;
    private final TrackData track;
    private final long time;
    private final int league;
    private final String date;
    private final String author;

    private List<EngineStateRecord> states = new ArrayList<>();

    public TrackRecord(TrackData track, long time, int league, String author) {
        this.track = track;
        this.trackGuid = track.getGuid();
        this.trackName = track.getName();
        this.league = league;
        this.time = time;
        this.date = Constants.DATE_TIME_FORMAT.format(new Date());
        this.author = author;
    }

    public void setStates(List<EngineStateRecord> states) {
        this.states = states;
    }

    public int getLeague() {
        return league;
    }

    public String getTrackGuid() {
        return trackGuid;
    }

    public List<EngineStateRecord> getStates() {
        return states;
    }

    public String getTrackName() {
        return trackName;
    }

    public TrackData getTrack() {
        return track;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public String getAuthor() {
        return author;
    }
}