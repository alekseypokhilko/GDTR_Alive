package org.happysanta.gdtralive.game.external;

import org.happysanta.gdtralive.game.modes.MenuData;

public interface GdMenu {
    void menuToGame();

    boolean canStartTrack();

    void keyPressed(int k);

    void showMenu(MenuData data);

    boolean isCurrentMenuEmpty();

}
