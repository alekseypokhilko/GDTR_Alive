package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.model.MenuData;

import java.io.Serializable;

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

    @Override
    public boolean isMenuDisabled() {
        return false;
    }

    @Override
    public void setCurrentMenu(Serializable newMenu) {

    }

    @Override
    public void onCurrentMenuScroll(double p) {

    }

    @Override
    public void back() {

    }

    @Override
    public void menuBack() {

    }

    @Override
    public void setM_blZ(boolean flag) {

    }

    @Override
    public void setMenuDisabled(boolean flag) {

    }

    @Override
    public void destroy() {

    }
}