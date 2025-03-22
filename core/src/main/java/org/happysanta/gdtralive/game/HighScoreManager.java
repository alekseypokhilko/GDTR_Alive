package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.dto.ScoreDto;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.Score;
import org.happysanta.gdtralive.game.http.APIClient;
import org.happysanta.gdtralive.game.util.Mapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HighScoreManager {

    private final GdDataSource dataSource;
    private final Application application;

    public HighScoreManager(Application application, GdDataSource dataSource) {
        this.dataSource = dataSource;
        this.application = application;
    }

    public HighScores getHighScores(String trackId, int league) {
        try {
            String url = application.getServerConfig().url();

            //todo lower version
            CompletableFuture<List<ScoreDto>> future = CompletableFuture.supplyAsync(
                    () -> APIClient.serverCall(url, api -> api.trackScores(trackId, league, application.getGame().getParams().getRoomId()))
            );
            List<ScoreDto> scoreDtos = future.get(3, TimeUnit.SECONDS);
            HighScores highScores = new HighScores(league);
            List<Score> scores = highScores.get(league);
            for (ScoreDto dto : scoreDtos) {
                scores.add(Mapper.fromDto(dto));
            }
            return highScores;
        } catch (Exception e) {
            e.printStackTrace(); //todo
        }
        return getLocalHighScores(trackId, league);
    }

    public HighScores getLocalHighScores(String trackId, int league) {
        return dataSource.getHighScores(trackId, league);
    }

    public List<String> getFormattedScores(String trackGuid, int league) {
        return getHighScores(trackGuid, league).getFormattedScores(league);
    }

    public List<String> getFormattedScores(int league, int level, int track) {
        return getHighScores(application.getModManager().getTrackId(level, track), league).getFormattedScores(league);
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
