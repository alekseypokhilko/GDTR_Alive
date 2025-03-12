package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.api.dto.TrackParams;

public interface GdTrackEditor {
    void init(Game game);
    void createNew(String playerName);

    void startEditing();

    void exitEditor();

    void playTrack();

    void saveLeagueInput(int league);

    void saveTrack();

    TrackParams getCurrentTrack();
    void setCurrentTrack(TrackParams params);

    void showLayout();

    void hideLayout();
}
