package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.util.Utils;

public class TrackParams {
    private GameTheme gameTheme;
    private TrackData data;

    public TrackParams() {
    }

    public TrackParams(TrackData data) {
        this.data = data;
    }

    public TrackData getData() {
        return data;
    }

    public void setData(TrackData data) {
        this.data = data;
    }

    public GameTheme getGameTheme() {
        if (gameTheme == null) {
            gameTheme = new GameTheme();
        }
        return gameTheme;
    }

    public void setGameTheme(GameTheme gameTheme) {
        this.gameTheme = gameTheme;
    }

    public TrackParams pack() {
        TrackParams trackParams = new TrackParams();
        trackParams.setGameTheme(gameTheme);
        trackParams.setData(data.unpacked ? Utils.packTrack(data) : data);
        return trackParams;
    }
}
