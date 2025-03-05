package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;

import java.util.ArrayList;
import java.util.HashMap;

public class DesktopGdDataSource implements GdDataSource {
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

    }

    @Override
    public java.util.List<ModEntity> getAllLevels() {
        return null;
    }

    @Override
    public ModEntity getMod(long id) {
        ModEntity modEntity = new ModEntity();
        modEntity.setId(id);
        return modEntity;
    }

    @Override
    public boolean isDefaultModCreated() {
        return false;
    }

    @Override
    public HighScores getHighScores(String levelGuid, int league) {
        return null;
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
        return null;
    }

    @Override
    public java.util.List<ModEntity> getLevels(int offset, int count) {
        return null;
    }

    @Override
    public boolean isApiIdInstalled(long apiId) {
        return false;
    }

    int i = 0;
    @Override
    public ModEntity createMod(ModEntity mod) {
        i =i + 1;
        mod.setId(i);
        return mod;
    }
}