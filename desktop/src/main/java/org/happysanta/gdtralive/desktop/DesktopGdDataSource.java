package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DesktopGdDataSource implements GdDataSource {
    ModEntity level;

    @Override
    public void open() throws Exception {

    }

    @Override
    public void close() {

    }

    @Override
    public void resetAllLevelsSettings() {

    }

    @Override
    public void updateMod(ModEntity level) {
        this.level = level;
    }

    @Override
    public java.util.List<ModEntity> getAllLevels() {
        ArrayList<ModEntity> list = new ArrayList<>();
        list.add(level);
        return list;
    }

    @Override
    public ModEntity getMod(long id) {
        level.setId(id);
        return level;
    }

    @Override
    public boolean isDefaultModCreated() {
        return false;
    }

    @Override
    public HighScores getHighScores(String levelGuid, int league) {
        HighScores highScores = new HighScores(league);
        List<Score> scores = highScores.get(league);
        scores.add(getScore(levelGuid, league));
        scores.add(getScore(levelGuid, league));
        scores.add(getScore(levelGuid, league));
        scores.add(getScore(levelGuid, league));
        scores.add(getScore(levelGuid, league));
        scores.add(getScore(levelGuid, league));
        return highScores;
    }

    private static Score getScore(String levelGuid, int league) {
        Score t = new Score();
        t.setLeague(league);
        t.setLevelGuid(levelGuid);
        t.setName("AAA");
        t.setTime(123);
        return t;
    }

    @Override
    public void saveHighScore(Score score) {

    }

    @Override
    public void clearHighScores(String levelGuid) {

    }

    @Override
    public void deleteLevel(ModEntity level) {

    }

    @Override
    public void deleteAllLevels() {

    }

    @Override
    public HashMap<Long, Long> findInstalledLevels(ArrayList<Long> apiIds) {
        return null;
    }

    @Override
    public ModEntity getMod(String guid) {
        level.setGuid(guid);
        return level;
    }

    @Override
    public java.util.List<ModEntity> getLevels(int offset, int count) {
        ArrayList<ModEntity> list = new ArrayList<>();
        list.add(level);
        return list;
    }

    @Override
    public boolean isApiIdInstalled(long apiId) {
        return false;
    }

    int i = 0;

    @Override
    public ModEntity createMod(ModEntity mod) {
        i = i + 1;
        mod.setId(i);
        level = mod;
        return level;
    }
}