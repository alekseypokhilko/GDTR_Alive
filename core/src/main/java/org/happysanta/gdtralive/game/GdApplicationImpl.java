package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdGameView;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;
import org.happysanta.gdtralive.game.api.external.GdStr;

/**
 * Core application logic
 */
public class GdApplicationImpl implements GdApplication {
    private Game game;
    private GdMenu menu;

    private final GdPlatform platform;
    private final GdSettings settings;
    private final GdFileStorage fileStorage;
    private final GdDataSource dataSource;
    private final GdStr str;
    private final GdGameView gameView;
    private final ModManager modManager;
    private final HighScoreManager highScoreManager;

    private boolean wasPaused = false;
    private boolean wasStarted = false;
    private boolean wasDestroyed = false;
    private boolean alive = false;
    private boolean pause = true;
    private boolean menuShown = false;
    private boolean fullResetting = false;
    private boolean exiting = false;

    private boolean inited = false;
    private Thread thread;

    public GdApplicationImpl(GdPlatform platform, GdSettingsStorage settingsStorage, GdStr str,
                             GdFileStorage fileStorage, GdDataSource dataSource, GdGameView gdGameView) {
        this.platform = platform;
        this.highScoreManager = new HighScoreManager(this, dataSource);
        this.settings = new GdSettingsImpl(settingsStorage);
        this.modManager = new ModManager(fileStorage, settings, dataSource, platform.getDensity());
        this.gameView = gdGameView;
        this.str = str;
        this.fileStorage = fileStorage;
        this.dataSource = dataSource;

        fileStorage.setApplication(this);
        thread = null;
    }

    @Override
    public void setMenu(GdMenu menu) {
        this.menu = menu;
    }

    @Override
    public void doStart() {
        Thread.currentThread().setName("main_thread");
        alive = true;
        pause = false;

        if (thread == null) {
            thread = new Thread(this::run);
            thread.setName("game_thread");
            thread.start();
        }

        wasStarted = true;
    }

    private void run() {
        if (!inited) {
            try {
                int width = gameView.getGdWidth();
                int height = gameView.getGdHeight();
                game = new Game(this, width, height);
                gameView.setGdView(game.getView());
                platform.init();
                game.init(platform.getMenu());
                new SplashScreen().showSplashScreens(game.getView());
                inited = true;
            } catch (Exception _ex) {
                _ex.printStackTrace();
                throw new RuntimeException(_ex);
            }
        }

        game.gameLoop();
        destroyApp(false);
    }

    @Override
    public void onResume() {
        if (wasPaused && wasStarted) {
            pause = false;
            wasPaused = false;
        }
    }

    @Override
    public void onPause() {
        wasPaused = true;
        pause = true;
        if (!menuShown && inited)
            gameToMenu();

        try {
            modManager.saveModState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (gameView != null && menu != null && inited) {
            if (menuShown)
                menu.back();
            else
                showMenu();
        }
    }

    @Override
    public void onKeyDown(int keyCode) {
        game.getKeyboardHandler().mappedKeyPressed(keyCode);
    }

    @Override
    public void onKeyUp(int keyCode) {
        game.getKeyboardHandler().mappedKeyReleased(keyCode);
    }

    @Override
    public void showMenu() {
        if (menu != null) {
            menu.setM_blZ(true);
            gameToMenu();
        }
    }

    @Override
    public boolean isMenuShown() {
        return menuShown;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean isOnPause() {
        return pause;
    }

    @Override
    public void notify(String message) {
        platform.notify(message);
    }

    @Override
    public void setFullResetting(boolean fullResetting) {
        this.fullResetting = fullResetting;
    }

    @Override
    public void destroyApp(final boolean restart) {
        if (wasDestroyed) {
            return;
        }

        wasDestroyed = true;
        alive = false;

        platform.runOnUiThread(() -> {
            inited = false;

            synchronized (gameView) {
                destroyResources();

                if (exiting || restart) {
                    platform.finish();
                }

                if (restart) {
                    platform.doRestartApp();
                }
            }
        });
    }

    private void destroyResources() {
        if (gameView != null) gameView.getGdView().destroy();

        menuShown = false;
        if (menu != null) {
            menu.destroy();
        }

        if (dataSource != null) {
            dataSource.close();
        }

        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public GdFileStorage getFileStorage() {
        return fileStorage;
    }

    @Override
    public GdStr getStr() {
        return str;
    }

    @Override
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public GdMenu getMenu() {
        return menu;
    }

    @Override
    public GdSettings getSettings() {
        return settings;
    }

    @Override
    public void gameToMenu() {
        if (gameView == null) {
            return;
        }

        game.pause();

        menuShown = true;

        if (!settings.isKeyboardInMenuEnabled())
            platform.hideKeyboardLayout();
        else
            platform.showKeyboardLayout();

        platform.gameToMenuUpdateUi();
    }

    @Override
    public void menuToGame() {
        game.resume();

        menuShown = false;
        if (!settings.isKeyboardInMenuEnabled())
            platform.hideKeyboardLayout();
        else
            platform.showKeyboardLayout();

        platform.menuToGameUpdateUi();
    }

    @Override
    public void exit() {
        exiting = true;
        alive = false;
        if (!menu.isCurrentMenuEmpty()) {
            menu.menuBack();
        } else {
            menu.setCurrentMenu(null);
        }
    }

    @Override
    public void trainingMode() {
        platform.trainingMode();
    }

    @Override
    public void editMode() {
        menuToGame();
        platform.hideKeyboardLayout();
    }

    @Override
    public boolean isInited() {
        return inited;
    }
}