package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.Constants;

import java.util.Date;

public class Score {
    private String trackId; //Utils.getTrackId()
    private int league;
    private long time;
    private String name;
    private String date;

    public Score() {
        this.date = Constants.DATE_FORMAT.format(new Date());
    }

    public int getLeague() {
        return league;
    }

    public void setLeague(int league) {
        this.league = league;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
