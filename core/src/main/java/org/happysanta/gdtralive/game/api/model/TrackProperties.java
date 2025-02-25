package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.LeaguePropertiesTheme;

public class TrackProperties {
    public GameTheme gameProperties;
    public LeaguePropertiesTheme leagueProperties;

    public GameTheme getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(GameTheme gameProperties) {
        this.gameProperties = gameProperties;
    }

    public LeaguePropertiesTheme getLeagueProperties() {
        return leagueProperties;
    }

    public void setLeagueProperties(LeaguePropertiesTheme leagueProperties) {
        this.leagueProperties = leagueProperties;
    }
}
