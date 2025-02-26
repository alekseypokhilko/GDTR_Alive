package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.HighScoreManager;

public interface GdApplication {
    GdDataSource getDataSource();
    GdFileStorage getFileStorage();
    GdUtils getUtils();
    ModManager getModManager();
    HighScoreManager getHighScoreManager();
    Game getGame();
    GdMenu getMenu();
    GdSettings getSettings();

    boolean isMenuShown();
    boolean isAlive();
    boolean isOnPause();
    void gameToMenu();
    void menuToGame();
    void trainingMode();
    void exit();
    void notify(String message);
}
