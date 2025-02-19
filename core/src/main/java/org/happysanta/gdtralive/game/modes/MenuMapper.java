package org.happysanta.gdtralive.game.modes;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.storage.ModEntity;

public class MenuMapper {
    private final Game game;

    public MenuMapper(Game game) {
        this.game = game;
    }

    public MenuData mapInGameMenuData() {
        MenuData inGameMenu = new MenuData();
        inGameMenu.setMenuMode(MenuMode.IN_GAME);
        inGameMenu.setGameMode(game.getMode());
        inGameMenu.setTrackName(game.getCurrentTrackName());
        return inGameMenu;
    }

    public MenuData getFinishedMenuData(long lastTrackTime, int selectedLevel, int selectedTrack, int selectedLeague) {
        ModEntity level = game.getLevelsManager().getCurrentLevel();
        MenuData data = new MenuData();
        data.setMenuMode(MenuMode.FINISHED);
        data.setGameMode(game.getMode());
        data.setTrackName(game.getCurrentTrackName());
        data.setTrackGuid(game.getCurrentTrackGuid());
        data.setLastTrackTime(lastTrackTime);
        data.setSelectedLevel(selectedLevel);
        data.setSelectedTrack(selectedTrack);
        data.setSelectedLeague(selectedLeague);
        data.setNewSelectedTrack(level.getSelectedTrack());
        data.setNewSelectedLevel(level.getSelectedLevel());
        data.setNewSelectedLeague(level.getSelectedLeague());
        data.setNewUnlockedLeagueCount(level.getUnlockedLeagues());
        data.setNewUnlockedLevelCount(level.getUnlockedLevels());
        data.setNewUnlockedTrackCount(level.getUnlockedTracksCount(level.getSelectedLevel()));
        return data;
    }
}
