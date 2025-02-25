package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.model.ModEntity;

import java.util.ArrayList;
import java.util.List;

public class LevelsManager {

    private final GdDataSource dataSource;
    private ModEntity currentLevel;
    boolean temporallyUnlocked = false;

    public LevelsManager(GdSettings settings, GdDataSource levelsDataSource) {
        dataSource = levelsDataSource;

        try {
            dataSource.open();

            if (!dataSource.isDefaultLevelCreated()) {
                List<Integer> counts = new ArrayList<>();
                counts.add(10);
                counts.add(10);
                counts.add(10);
                ModEntity level = dataSource.createLevel(
                        "GDTR original", "Codebrew Software", counts, 0, 0, true, 1);
                settings.setLevelId(level.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return;
        }

        //reload
        long id = settings.getLevelId();
        //currentLevel = dataSource.getLevel(id);
        //todo fix level loading
        currentLevel = new ModEntity();
        currentLevel.setName("GD FIX ME");
        currentLevel.setUnlockedTracks("[0,0,-1]");
        currentLevel.setTrackCountsByLevel("[4,4,4]");
//        currentLevel.unlockAllTracks();
    }

    public ModEntity getCurrentLevel() {
        return currentLevel;
    }

    public ModEntity getLeveL(long id) {
        return dataSource.getLevel(id);
    }

    public ModEntity[] getAllInstalledLevels() {
        return dataSource.getAllLevels().toArray(new ModEntity[0]);
    }

    public void updateLevelSettings() {
        if (temporallyUnlocked) {
            return;
        }
        if (true) {
            return; //todo delete
        }
        dataSource.updateLevel(currentLevel);
    }

    public void setTemporallyUnlocked(boolean temporallyUnlocked) {
        this.temporallyUnlocked = temporallyUnlocked;
    }
}
