package org.happysanta.gdtralive.game.external;

import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.mod.ModManager;
import org.happysanta.gdtralive.game.storage.HighScoreManager;

public interface GdApplication {
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
    void exit();
    void notify(String message);
}
