package org.happysanta.gdtralive.game.mod;

public class TrackProperties {
    public GameTheme gameProperties;
    public LeagueProperties leagueProperties;

    public GameTheme getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(GameTheme gameProperties) {
        this.gameProperties = gameProperties;
    }

    public LeagueProperties getLeagueProperties() {
        return leagueProperties;
    }

    public void setLeagueProperties(LeagueProperties leagueProperties) {
        this.leagueProperties = leagueProperties;
    }
}
