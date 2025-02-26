package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface GdDataSource {

    void open() throws Exception;

    void close();

    ModEntity createLevel(String guid, String name, String author, List<Integer> tracksCount, long addedTs, long installedTs, boolean isDefault, long apiId);

    void resetAllLevelsSettings();

    void updateLevel(ModEntity level);

    List<ModEntity> getAllLevels();

    ModEntity getLevel(long id);

    boolean isDefaultLevelCreated();

    HighScores getHighScores(String levelGuid, int league);

    void saveHighScore(Score score);

    void clearHighScores(String levelGuid);

    /// todo delete / refactor
    void deleteLevel(ModEntity level);
    void deleteAllLevels();

    HashMap<Long, Long> findInstalledLevels(ArrayList<Long> apiIds);

    ModEntity getLevel(String guid);

    List<ModEntity> getLevels(int offset, int count);

    boolean isApiIdInstalled(long apiId);
}
