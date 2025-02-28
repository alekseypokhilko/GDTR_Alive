package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.TrackParams;

public class TrackReference {
    private String name;
    private String guid;
    private GameTheme gameTheme;
    private LeagueTheme leagueTheme;
    private TrackParams data;

    public TrackParams getData() {
        return data;
    }

    public void setData(TrackParams data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public GameTheme getGameTheme() {
        return gameTheme;
    }

    public void setGameTheme(GameTheme gameTheme) {
        this.gameTheme = gameTheme;
    }

    public LeagueTheme getLeagueTheme() {
        return leagueTheme;
    }

    public void setLeagueTheme(LeagueTheme leagueTheme) {
        this.leagueTheme = leagueTheme;
    }
}
