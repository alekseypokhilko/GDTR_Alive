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

    ModEntity createMod(ModEntity mod);

    ModEntity getMod(String guid);

    void resetAllLevelsSettings();

    void updateMod(ModEntity mod);

    List<ModEntity> getAllLevels();

    ModEntity getMod(long id);

    boolean isDefaultModCreated();

    HighScores getHighScores(String levelGuid, int league);

    void saveHighScore(Score score);

    void clearHighScores(String levelGuid);

    /// todo delete / refactor
    void deleteLevel(ModEntity level);
    void deleteAllLevels();

    HashMap<Long, Long> findInstalledLevels(ArrayList<Long> apiIds);

    List<ModEntity> getLevels(int offset, int count);

    boolean isApiIdInstalled(long apiId);
}
