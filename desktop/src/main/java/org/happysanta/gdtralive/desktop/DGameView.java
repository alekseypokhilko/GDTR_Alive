package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.DesktopGdView;
import org.happysanta.gdtralive.game.GdView;
import org.happysanta.gdtralive.game.api.external.GdGameView;

public class DGameView implements GdGameView {
    @Override
    public int getGdWidth() {
        return DesktopGdView.width;
    }

    @Override
    public int getGdHeight() {
        return DesktopGdView.height;
    }

    @Override
    public void setGdView(GdView gdView) {

    }

    @Override
    public GdView getGdView() {
        return null;
    }
}
