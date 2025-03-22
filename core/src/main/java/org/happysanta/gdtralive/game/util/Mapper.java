package org.happysanta.gdtralive.game.util;

import org.happysanta.gdtralive.game.api.MenuMode;
import org.happysanta.gdtralive.game.api.dto.ScoreDto;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;

public class Mapper {
    public static MenuData mapInGameMenuData(GameParams params) {
        MenuData inGameMenu = new MenuData();
        inGameMenu.setMenuMode(MenuMode.IN_GAME);
        inGameMenu.setGameMode(params.getMode());
        inGameMenu.setTrackName(params.getTrackData().name);
        return inGameMenu;
    }

    public static MenuData getFinishedMenuData(GameParams params, long lastTrackTime, ModEntity level, int attemptCount) {
        MenuData data = new MenuData();
        data.setMenuMode(MenuMode.FINISHED);
        data.setGameMode(params.getMode());
        data.setAttemptCount(attemptCount);
        data.setTrackName(params.getTrackData().name);
        data.setTrackId(Utils.getTrackId(params.getTrackData()));
        data.setLastTrackTime(lastTrackTime);
        data.setSelectedLevel(params.getLevel());
        data.setSelectedTrack(params.getTrack());
        data.setSelectedLeague(params.getLeague());
        data.setNewSelectedTrack(level.getSelectedTrack());
        data.setNewSelectedLevel(level.getSelectedLevel());
        data.setNewSelectedLeague(level.getSelectedLeague());
        data.setNewUnlockedLeagueCount(level.getUnlockedLeagues());
        data.setNewUnlockedLevelCount(level.getUnlockedLevels());
        data.setNewUnlockedTrackCount(level.getUnlockedTracksCount(level.getSelectedLevel()));
        return data;
    }

    public static ModEntity mapModToEntity(Mod mod, boolean isDefault) {
        long now = System.currentTimeMillis();
        ModEntity entity = new ModEntity();
        entity.setGuid(mod.getGuid());
        entity.setName(mod.getName());
        entity.setAuthor(mod.getAuthor());
        entity.setAddedTs(now);
        entity.setInstalledTs(now);
        entity.setApiId(1L);
        entity.setDefault(isDefault);
        entity.setTrackCountsByLevel(mod.getTrackCounts());
        entity.initIfClear();
        return entity;
    }

    public static Score fromDto(ScoreDto dto) {
        Score score = new Score();
        score.setTrackId(dto.getTrackId());
        score.setLeague(dto.getLeague());
        score.setTime(dto.getTime());
        score.setName(dto.getName());
        score.setDate(dto.getDate());
        return score;
    }

    public static ScoreDto toDto(Score score, String roomId) {
        ScoreDto scoreDto = new ScoreDto();
        if (roomId != null) {
            scoreDto.setRoomId(roomId);
        }
        scoreDto.setDate(score.getDate());
        scoreDto.setName(score.getName());
        scoreDto.setTime(score.getTime());
        scoreDto.setLeague(score.getLeague());
        scoreDto.setTrackId(score.getTrackId());
        return scoreDto;
    }
}
