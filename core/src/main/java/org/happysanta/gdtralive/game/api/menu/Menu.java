package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuMode;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.model.MenuData;

public class Menu<T> implements GdMenu<T> {
    private final Application application;
    private final MenuFactory<T> menuFactory;

    public MenuScreen<T> currentMenu;
    public boolean m_blZ = false;
    public boolean menuDisabled = false;
    private boolean m_SZ = false;
    public Menu(Application application, MenuFactory<T> menuFactory) {
        this.application = application;
        this.menuFactory = menuFactory;
    }

    public void menuToGame() {
        m_SZ = true;
        application.menuToGame();
    }

    // not sure about this name
    public boolean canStartTrack() {
        if (m_SZ) {
            m_SZ = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showMenu(MenuData data) {
        preShowMenu();

        if (MenuMode.MAIN == data.getMenuMode()) {
            showMainMenu();
        }
        if (MenuMode.IN_GAME == data.getMenuMode()) {
            showInGameMenu(data);
        }
        if (MenuMode.FINISHED == data.getMenuMode()) {
            showFinishedMenu(data);
        }

        application.getGame().menuBackgroundLoop();
        checkExit();
    }

    private void showMainMenu() {
        setCurrentMenu(menuFactory.get(MenuType.MAIN));
        application.getGame().startAutoplay(false);
        m_SZ = true;
    }

    private void showInGameMenu(MenuData data) {
        m_SZ = false;
        MenuScreen<T> screen = menuFactory.get(data.getGameMode().inGame);
        if (screen != null) {
            setCurrentMenu(screen.build(data));
        } else {
            throw new IllegalStateException("FIX ME: " + data.getGameMode().inGame);
        }
    }

    private void showFinishedMenu(MenuData data) {
        MenuScreen<T> screen = menuFactory.get(data.getGameMode().finished);
        if (screen != null) {
            setCurrentMenu(screen.build(data));
        } else {
            throw new IllegalStateException("FIX ME: " + data.getGameMode().inGame);
        }
        application.getGame().startAutoplay(false); //todo replay track
    }

    private void preShowMenu() {
        m_blZ = false;
        menuDisabled = false;
    }

    private void checkExit() {
        if (currentMenu == null && application.isAlive()) {
            application.exit();
        }
    }

    public void keyPressed(int k) {
        if (currentMenu != null && !menuDisabled)
            currentMenu.performAction(KeyboardHandler.getGameAction(k));
    }

    public void back() {
        if (currentMenu == menuFactory.get(GameMode.CAMPAIGN.inGame)) {
            application.menuToGame();
            return;
        }
        if (!isCurrentMenuEmpty())
            this.setCurrentMenu(currentMenu.getParent());
    }

    @Override
    public boolean isCurrentMenuEmpty() {
        return currentMenu == null;
    }

    public MenuScreen<T> getCurrentMenu() {
        return currentMenu;
    }

    @Override
    public void setM_blZ(boolean flag) {
        m_blZ = flag;
    }

    public void onCurrentMenuScroll(double p) {
        currentMenu.onScroll(p);
    }

    public void setMenuDisabled(boolean menuDisabled) {
        this.menuDisabled = menuDisabled;
    }

    @Override
    public void menuBack() {
        this.setCurrentMenu(currentMenu.getParent());
    }

    //this is magic
    //I am unable to understand and refactor this
    @Override
    public void setCurrentMenu(MenuScreen<T> newMenu) {
        menuDisabled = false;
        currentMenu = newMenu;
        if (!isCurrentMenuEmpty()) {
            currentMenu.performBeforeShowAction();
            application.getPlatform().setMenu(currentMenu);
            currentMenu.onShow();
        }

        m_blZ = false;
    }

    public synchronized void destroy() {
        currentMenu = null;
    }
}
