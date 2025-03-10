package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdGameView;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.http.APIClient;
import org.happysanta.gdtralive.game.http.ConfigApi;
import org.happysanta.gdtralive.game.http.ServerConfig;

/**
 * Core application logic
 */
public class Application {
    private Game game;
    private GdMenu menu;
    private ServerConfig serverConfig;

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
    private volatile boolean alive = false;
    private volatile boolean pause = true;
    private volatile boolean menuShown = false;
    private boolean fullResetting = false;
    private boolean exiting = false;

    private boolean inited = false;
    private Thread thread;

    public Application(GdPlatform platform, GdSettingsStorage settingsStorage, GdStr str,
                       GdFileStorage fileStorage, GdDataSource dataSource, GdGameView gdGameView) {
        this.platform = platform;
        this.highScoreManager = new HighScoreManager(this, dataSource);
        this.settings = new GdSettingsImpl(settingsStorage);
        this.modManager = new ModManager(fileStorage, settings, dataSource, platform.getDensity(), this);
        this.gameView = gdGameView;
        this.str = str;
        this.fileStorage = fileStorage;
        this.dataSource = dataSource;
        try {
            this.serverConfig = APIClient.fetchConfig(ConfigApi::serverConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileStorage.setApplication(this);
        thread = null;
    }

    public GdPlatform getPlatform() {
        return platform;
    }

    public void setMenu(GdMenu menu) {
        this.menu = menu;
    }

    
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

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void onResume() {
        if (wasPaused && wasStarted) {
            pause = false;
            wasPaused = false;
        }
    }

    
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

    
    public void onBackPressed() {
        if (gameView != null && menu != null && inited) {
            if (menuShown)
                menu.back();
            else
                showMenu();
        }
    }

    
    public void onKeyDown(int keyCode) {
        game.getKeyboardHandler().mappedKeyPressed(keyCode);
    }

    
    public void onKeyUp(int keyCode) {
        game.getKeyboardHandler().mappedKeyReleased(keyCode);
    }

    
    public void showMenu() {
        if (menu != null) {
            menu.setM_blZ(true);
            gameToMenu();
        }
    }

    
    public boolean isMenuShown() {
        return menuShown;
    }

    
    public boolean isAlive() {
        return alive;
    }

    
    public boolean isOnPause() {
        return pause;
    }

    
    public void notify(String message) {
        platform.notify(message);
    }

    
    public void setFullResetting(boolean fullResetting) {
        this.fullResetting = fullResetting;
    }

    
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

    
    public GdFileStorage getFileStorage() {
        return fileStorage;
    }

    
    public GdStr getStr() {
        return str;
    }

    
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    
    public ModManager getModManager() {
        return modManager;
    }

    
    public Game getGame() {
        return game;
    }

    
    public GdMenu getMenu() {
        return menu;
    }

    
    public GdSettings getSettings() {
        return settings;
    }

    
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

    
    public void menuToGame() {
        game.resume();

        menuShown = false;
        if (!settings.isKeyboardInMenuEnabled()) {
            platform.hideKeyboardLayout();
        }
        else {
            platform.showKeyboardLayout();
        }

        platform.menuToGameUpdateUi();
    }

    
    public void exit() {
        exiting = true;
        alive = false;
        if (!menu.isCurrentMenuEmpty()) {
            menu.menuBack();
        } else {
            menu.setCurrentMenu(null);
        }
    }

    
    public void trainingMode() {
        platform.trainingMode();
    }

    
    public void editMode() {
        menuToGame();
        platform.hideKeyboardLayout();
    }

    public void fullReset() {
        settings.resetAll();
        highScoreManager.resetAllLevelsSettings();
        highScoreManager.clearAllHighScores();

        setFullResetting(true);
        destroyApp(true);
    }
}