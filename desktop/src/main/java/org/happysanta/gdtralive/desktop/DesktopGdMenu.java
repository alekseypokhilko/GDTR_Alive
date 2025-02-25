package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.model.MenuData;

public class DesktopGdMenu implements GdMenu {
    @Override
    public void menuToGame() {

    }

    @Override
    public boolean canStartTrack() {
        return true;
    }

    @Override
    public void keyPressed(int k) {

    }

    @Override
    public void showMenu(MenuData data) {

    }

    @Override
    public boolean isCurrentMenuEmpty() {
        return false;
    }
}