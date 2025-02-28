package org.happysanta.gdtralive.game.api.dto;

import java.util.ArrayList;
import java.util.List;

public class LevelPack {
    private final List<TrackReference> tracks = new ArrayList<>();

    public List<TrackReference> getTracks() {
        return tracks;
    }
}
