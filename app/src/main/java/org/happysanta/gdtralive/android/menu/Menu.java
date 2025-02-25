package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.logDebug;
import static org.happysanta.gdtralive.android.Helpers.s;

import android.util.Log;

import org.happysanta.gdtralive.android.GDActivity;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.MenuMode;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class Menu implements GdMenu, MenuHandler {
    private final GdApplication application;
    private final MenuFactory menuFactory;

    public MenuScreen currentMenu;
    public boolean m_blZ = false;
    public boolean menuDisabled = false;
    private boolean m_SZ = false;
    public Menu(GdApplication application, MenuFactory menuFactory) {
        this.application = application;
        this.menuFactory = menuFactory;
    }

    public void menuToGame() {
        m_SZ = true;
        application.menuToGame();
    }

    public MenuAction createAction(int action, ActionHandler actionHandler) {
        return new MenuAction(s(MenuUtils.getActionText(action)), action, this, actionHandler);
    }

    public MenuAction backAction(Runnable beforeBack) {
        beforeBack.run();
        return createAction(MenuAction.BACK, item -> this.menuBack());
    }

    public MenuAction backAction() {
        return createAction(MenuAction.BACK, item -> this.menuBack());
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
        Log.d("Menu", data.toString());
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
        MenuScreen screen = menuFactory.get(data.getGameMode().inGame);
        if (screen != null) {
            setCurrentMenu(screen.build(data));
        } else {
            throw new IllegalStateException("FIX ME: " + data.getGameMode().inGame);
        }
    }

    private void showFinishedMenu(MenuData data) {
        MenuScreen screen = menuFactory.get(data.getGameMode().finished);
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
        logDebug("[Menu.showMenu] out loop");
        if (currentMenu == null && getGDActivity() != null) {
            logDebug("[Menu.showMenu] currentMenu == null, set alive = false");
            application.exit();
        }
    }

    public void keyPressed(int k) {
        if (currentMenu != null && !menuDisabled)
            currentMenu.performAction(KeyboardHandler.getGameAction(k));
    }

    public void back() {
        if (currentMenu == menuFactory.get(GameMode.CLASSIC.inGame)) {
            application.menuToGame();
            return;
        }
        if (!isCurrentMenuEmpty())
            setCurrentMenu(currentMenu.getParent());
    }

    @Override
    public boolean isCurrentMenuEmpty() {
        return currentMenu == null;
    }

    public MenuScreen getCurrentMenu() {
        return currentMenu;
    }

    public void menuBack() {
        setCurrentMenu(currentMenu.getParent());
    }

    //this is magic
    //I am unable to understand and refactor this
    public void setCurrentMenu(MenuScreen newMenu) {
        menuDisabled = false;
        GDActivity gd = getGDActivity();
        currentMenu = newMenu;
        if (!isCurrentMenuEmpty()) {
            currentMenu.performBeforeShowAction();
            gd.setMenu(currentMenu.getLayout());
            currentMenu.onShow();
        }

        m_blZ = false;
    }

    public synchronized void destroy() {
        currentMenu = null;
    }
}
