package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.util.Utils;

public class TrackParams {
    private GameTheme gameTheme;
    private LeagueTheme leagueTheme;
    private TrackData data;

    public TrackParams() {
    }

    public TrackParams(TrackData data) {
        this.data = data;
    }

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

    public TrackParams pack() {
        TrackParams trackParams = new TrackParams();
        trackParams.setGameTheme(gameTheme);
        trackParams.setLeagueTheme(leagueTheme);
        trackParams.setData(data.unpacked ? Utils.packTrack(data) : data);
        return trackParams;
    }
}
