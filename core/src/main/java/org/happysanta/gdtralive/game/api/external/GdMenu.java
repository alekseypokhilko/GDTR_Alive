package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.model.MenuData;

import java.io.Serializable;

public interface GdMenu {
    void menuToGame();

    boolean canStartTrack();

    void keyPressed(int k);

    void showMenu(MenuData data);

    boolean isCurrentMenuEmpty();

    void setCurrentMenu(Serializable newMenu);

    void onCurrentMenuScroll(double p);

    void back();

    void menuBack();

    void setM_blZ(boolean flag);

    void setMenuDisabled(boolean flag);

    void destroy();
}
