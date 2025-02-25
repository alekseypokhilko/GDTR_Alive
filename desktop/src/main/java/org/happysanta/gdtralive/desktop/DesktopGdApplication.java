package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdUtils;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.HighScoreManager;

public class DesktopGdApplication implements GdApplication {
    @Override
    public GdFileStorage getFileStorage() {
        return null;
    }

    @Override
    public ModManager getModManager() {
        return null;
    }

    @Override
    public Game getGame() {
        return null;
    }

    @Override
    public GdMenu getMenu() {
        return null;
    }

    @Override
    public GdSettings getSettings() {
        return null;
    }

    @Override
    public boolean isMenuShown() {
        return false;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isOnPause() {
        return false;
    }

    @Override
    public void gameToMenu() {

    }

    @Override
    public void menuToGame() {

    }

    @Override
    public void exit() {

    }

    @Override
    public GdUtils getUtils() {
        return null;
    }

    @Override
    public HighScoreManager getHighScoreManager() {
        return null;
    }

    @Override
    public GdDataSource getDataSource() {
        return null;
    }

    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}