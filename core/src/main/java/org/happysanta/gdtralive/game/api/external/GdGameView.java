package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.GdView;

public interface GdGameView {
    int getGdWidth();

    int getGdHeight();

    void setGdView(GdView gdView);

    GdView getGdView();
}
