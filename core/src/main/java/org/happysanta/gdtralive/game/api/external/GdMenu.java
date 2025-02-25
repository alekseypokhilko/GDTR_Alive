package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.model.MenuData;

public interface GdMenu {
    void menuToGame();

    boolean canStartTrack();

    void keyPressed(int k);

    void showMenu(MenuData data);

    boolean isCurrentMenuEmpty();

}
