package org.happysanta.gdtralive.game.storage;

import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdDataSource;

import java.util.List;

public class HighScoreManager {

    private final GdDataSource dataSource;
    private final GdApplication application;

    public HighScoreManager(GdApplication application, GdDataSource dataSource) {
        this.dataSource = dataSource;
        this.application = application;
    }

    public HighScores getHighScores(String levelGuid, int league) {
        return dataSource.getHighScores(levelGuid, league);
    }

    public List<String> getFormattedScores(String trackGuid, int league) {
        return getHighScores(trackGuid, league).getFormattedScores(league);
    }

    public List<String> getFormattedScores(int league, int level, int track) {
        return getHighScores(application.getModManager().getTrackGuid(level, track), league).getFormattedScores(league);
    }

    public void saveHighScore(Score score) {
        dataSource.saveHighScore(score);
    }

    public void clearHighScores(String levelGuid) {
        dataSource.clearHighScores(levelGuid);
    }

    public void clearAllHighScores() {
        dataSource.clearHighScores(null);
    }

    public void resetAllLevelsSettings() {
        dataSource.resetAllLevelsSettings();
    }
}
