package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.api.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdStr;
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
    public GdStr getStr() {
        return null;
    }

    @Override
    public HighScoreManager getHighScoreManager() {
        return null;
    }

    @Override
    public void notify(String message) {
        System.out.println(message);
    }

    @Override
    public void doStart() {

    }

    @Override
    public void showMenu() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean isInited() {
        return false;
    }

    @Override
    public void setFullResetting(boolean fullResetting) {

    }

    @Override
    public void destroyApp(boolean restart) {

    }

    @Override
    public void trainingMode() {

    }

    @Override
    public void setMenu(GdMenu menu) {

    }

    @Override
    public void editMode() {

    }
}