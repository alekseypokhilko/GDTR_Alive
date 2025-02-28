package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.LeagueTheme;

public class TrackTheme {
    public GameTheme gameTheme;
    public LeagueTheme leagueTheme;

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
