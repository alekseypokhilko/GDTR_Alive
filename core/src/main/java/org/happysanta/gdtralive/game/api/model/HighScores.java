package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Fmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HighScores {

    private final Map<Integer, List<Score>> scores;

    public HighScores(int league) {
        this.scores = new HashMap<>();
        for (int i = 0; i < league + 1; i++) {
            //todo refactoring
            scores.put(i, new ArrayList<>());
        }
    }

    public HighScores() {
        this.scores = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            scores.put(i, new ArrayList<>());
        }
    }

    public List<String> getFormattedScores(int league) {
        List<String> formattedScores = new ArrayList<>();
        int i = 1;
        for (Score score : Objects.requireNonNull(scores.get(league))) {
            if (score.getTime() < 1) {
                continue;
            }
            long millis = score.getTime();
            String time = Fmt.durationString(millis);
            formattedScores.add(String.format("%d. %s - %s - %s", i, time, score.getName(), score.getDate()));
            i++;
        }
        return formattedScores;
    }

    public int getPlace(int league, long time) {
        List<Score> list = Objects.requireNonNull(scores.get(league));
        for (int i = 0; i < list.size(); i++) {
            Score score = list.get(i);
            if (time <= score.getTime()) {
                return i;
            }
        }
        return 10;
    }

    public List<Score> get(int league) {
        return scores.get(league);
    }

    public void add(Score score, int league) {
        scores.get(league).add(score);
        Collections.sort(scores.get(league), (s1, s2) -> s1.getTime().compareTo(s2.getTime()));
    }
}
