package org.happysanta.gdtralive.game.storage;

import org.happysanta.gdtralive.game.Constants;

import java.util.Date;

public class Score {
    private int league;
    private String levelGuid;
    private String name;
    private long time;
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

    public String getLevelGuid() {
        return levelGuid;
    }

    public void setLevelGuid(String levelGuid) {
        this.levelGuid = levelGuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
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
