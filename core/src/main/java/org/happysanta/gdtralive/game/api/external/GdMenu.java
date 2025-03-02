package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.menu.MenuHandler;
import org.happysanta.gdtralive.game.api.model.MenuData;

public interface GdMenu<T> extends MenuHandler<T> {
    void menuToGame();

    boolean canStartTrack();

    void keyPressed(int k);

    void showMenu(MenuData data);

    boolean isCurrentMenuEmpty();

    void onCurrentMenuScroll(double p);

    void back();

    void setM_blZ(boolean flag);

    void setMenuDisabled(boolean flag);

    void destroy();
}
