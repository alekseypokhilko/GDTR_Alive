package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.element.IToggleMenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;

public interface PlatformMenuElementFactory<T> {

    void setMenu(GdMenu<T> menu);

    MenuElement<T> emptyLine(boolean beforeAction);

    MenuElement<T> restartAction(String name, ActionHandler handler);

    MenuElement<T> text(String title);

    IMenuItemElement<T> menu(String title, MenuScreen<T> parent);

    MenuElement<T> actionContinue(ActionHandler handler);

    MenuScreen<T> screen(String title, MenuScreen<T> parent);

    MenuElement<T> createAction(int action, ActionHandler actionHandler);

    MenuElement<T> backAction(Runnable beforeBack);

    MenuElement<T> backAction();

    MenuElement<T> reatart(String title, ActionHandler handler);

    MenuElement<T> action(String title, int action, ActionHandler<MenuElement<T>> handler);

    MenuElement<T> action(String title, ActionHandler<MenuElement<T>> handler);

    MenuElement<T> textHtmlBold(String key, String value);

    MenuElement<T> textHtml(String text);

    IMenuItemElement<T> menu(String title, MenuScreen<T> parent, ActionHandler<IMenuItemElement<T>> handler);

    MenuElement<T> badge(int icon, String title);

    IInputTextElement<T> editText(String title, String value, ActionHandler<IInputTextElement<T>> handler);

    MenuElement<T> highScore(String title, int place, boolean padding);

    MenuElement<T> getItem(String text, boolean padding);

    OptionsMenuElement<T> selector(String title, int selected, String[] options, MenuScreen<T> parent, ActionHandler<OptionsMenuElement<T>> handler);

    IToggleMenuElement<T> toggle(String title, int selected, ActionHandler<IToggleMenuElement<T>> action);
}
