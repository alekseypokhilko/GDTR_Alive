package org.happysanta.gdtralive.game.api;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.HighScoreManager;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdStr;

public interface GdApplication {
    void doStart();

    GdFileStorage getFileStorage();

    GdStr getStr();

    ModManager getModManager();

    HighScoreManager getHighScoreManager();

    Game getGame();

    GdMenu getMenu();

    void showMenu();

    GdSettings getSettings();

    void onPause();

    void onResume();

    void onBackPressed();

    boolean isMenuShown();

    boolean isAlive();

    boolean isInited();

    boolean isOnPause();

    void setFullResetting(boolean fullResetting);

    void destroyApp(boolean restart);

    void gameToMenu();

    void menuToGame();

    void trainingMode();

    void exit();

    void notify(String message);

    void setMenu(GdMenu menu);

    void editMode();
}
