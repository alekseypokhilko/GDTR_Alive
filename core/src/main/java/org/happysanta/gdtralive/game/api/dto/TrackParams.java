package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.TrackData;

public class TrackParams {
    private GameTheme gameTheme;
    private LeagueTheme leagueTheme;
    private TrackData data;

    public TrackData getData() {
        return data;
    }

    public void setData(TrackData data) {
        this.data = data;
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
