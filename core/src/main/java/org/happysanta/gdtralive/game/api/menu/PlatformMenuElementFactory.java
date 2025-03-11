package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuActionElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public interface PlatformMenuElementFactory<T> {

    void setMenu(GdMenu<T> menu);
    ModManager getModManager();

    MenuElement<T> emptyLine(boolean beforeAction);

    MenuElement<T> restartAction(String name, ActionHandler handler);

    MenuElement<T> text(String title);

    IMenuItemElement<T> menu(String title, MenuScreen<T> parent);

    IMenuActionElement<T> actionContinue(ActionHandler<IMenuActionElement<T>> handler);

    MenuScreen<T> screen(String title, MenuScreen<T> parent);

    MenuElement<T> createAction(int action, ActionHandler actionHandler);

    MenuElement<T> backAction(Runnable beforeBack);

    MenuElement<T> backAction();

    MenuElement<T> reatart(String title, ActionHandler handler);

    IMenuActionElement<T> action(String title, int action, ActionHandler<IMenuActionElement<T>> handler);

    IMenuActionElement<T> action(String title, ActionHandler<IMenuActionElement<T>> handler, boolean reloadTheme);
    IMenuActionElement<T> action(String title, ActionHandler<IMenuActionElement<T>> handler);

    MenuElement<T> textHtmlBold(String key, String value);

    MenuElement<T> textHtml(String text);

    IMenuItemElement<T> menu(String title, MenuScreen<T> parent, ActionHandler<IMenuItemElement<T>> handler);

    MenuElement<T> badge(int icon, String title);

    IInputTextElement<T> editText(String title, String value, ActionHandler<IInputTextElement<T>> handler);

    MenuElement<T> highScore(String title, int place, boolean padding);

    MenuElement<T> getItem(String text, boolean padding);

    IOptionsMenuElement<T> selector(String title, int selected, String[] options, MenuScreen<T> parent, ActionHandler<IOptionsMenuElement<T>> handler);
    IOptionsMenuElement<T> color(String title, int selected, MenuScreen<T> parent, ActionHandler<IOptionsMenuElement<T>> handler);

    IToggleMenuElement<T> toggle(String title, int selected, ActionHandler<IToggleMenuElement<T>> action);
}
