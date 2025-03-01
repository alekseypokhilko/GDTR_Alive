package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.model.MenuData;

public interface GdMenu<T> {
    void menuToGame();

    boolean canStartTrack();

    void keyPressed(int k);

    void showMenu(MenuData data);

    boolean isCurrentMenuEmpty();

    void setCurrentMenu(MenuScreen<T> newMenu);

    void onCurrentMenuScroll(double p);

    void back();

    void menuBack();

    void setM_blZ(boolean flag);

    void setMenuDisabled(boolean flag);

    void destroy();
}
