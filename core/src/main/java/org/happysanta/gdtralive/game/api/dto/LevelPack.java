package org.happysanta.gdtralive.game.api.dto;

import java.util.ArrayList;
import java.util.List;

public class LevelPack {
    private List<TrackParams> tracks = new ArrayList<>();

    public List<TrackParams> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackParams> tracks) {
        this.tracks = tracks;
    }
}
