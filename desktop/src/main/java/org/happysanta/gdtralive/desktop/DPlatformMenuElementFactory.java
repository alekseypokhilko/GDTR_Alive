package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public class DPlatformMenuElementFactory implements PlatformMenuElementFactory<Object> {
    @Override
    public void setMenu(GdMenu<Object> menu) {

    }

    @Override
    public ModManager getModManager() {
        return null;
    }

    @Override
    public MenuElement<Object> emptyLine(boolean beforeAction) {
        return null;
    }

    @Override
    public MenuElement<Object> restartAction(String name, ActionHandler handler) {
        return null;
    }

    @Override
    public MenuElement<Object> text(String title) {
        return null;
    }

    @Override
    public IMenuItemElement<Object> menu(String title, MenuScreen<Object> objectMenuScreen) {
        return null;
    }

    @Override
    public IMenuActionElement<Object> actionContinue(ActionHandler<IMenuActionElement<Object>> handler) {
        return null;
    }

    @Override
    public MenuScreen<Object> screen(String title, MenuScreen<Object> objectMenuScreen) {
        return null;
    }

    @Override
    public MenuElement<Object> createAction(int action, ActionHandler actionHandler) {
        return null;
    }

    @Override
    public MenuElement<Object> backAction(Runnable beforeBack) {
        return null;
    }

    @Override
    public MenuElement<Object> backAction() {
        return null;
    }

    @Override
    public MenuElement<Object> reatart(String title, ActionHandler handler) {
        return null;
    }

    @Override
    public IMenuActionElement<Object> action(String title, int action, ActionHandler<IMenuActionElement<Object>> handler) {
        return null;
    }

    @Override
    public IMenuActionElement<Object> action(String title, ActionHandler<IMenuActionElement<Object>> handler) {
        return null;
    }

    @Override
    public MenuElement<Object> textHtmlBold(String key, String value) {
        return null;
    }

    @Override
    public MenuElement<Object> textHtml(String text) {
        return null;
    }

    @Override
    public IMenuItemElement<Object> menu(String title, MenuScreen<Object> objectMenuScreen, ActionHandler<IMenuItemElement<Object>> handler) {
        return null;
    }

    @Override
    public MenuElement<Object> badge(int icon, String title) {
        return null;
    }

    @Override
    public IInputTextElement<Object> editText(String title, String value, ActionHandler<IInputTextElement<Object>> handler) {
        return null;
    }

    @Override
    public MenuElement<Object> highScore(String title, int place, boolean padding) {
        return null;
    }

    @Override
    public MenuElement<Object> getItem(String text, boolean padding) {
        return null;
    }

    @Override
    public IOptionsMenuElement<Object> selector(String title, int selected, String[] options, MenuScreen<Object> objectMenuScreen, ActionHandler<IOptionsMenuElement<Object>> handler) {
        return null;
    }

    @Override
    public IToggleMenuElement<Object> toggle(String title, int selected, ActionHandler<IToggleMenuElement<Object>> action) {
        return null;
    }
}
