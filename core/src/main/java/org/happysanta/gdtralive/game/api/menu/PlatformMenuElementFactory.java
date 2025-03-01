package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.api.util.ActionHandler;

public interface PlatformMenuElementFactory<T> {

    void setMenu(MenuHandler<T> menu);

    MenuElement<T> emptyLine(boolean beforeAction);

    MenuElement<T> restartAction(String name, ActionHandler handler);

    MenuElement<T> text(String title);

    MenuElement<T> menuItem(String title, MenuScreen<T> parent);

    MenuElement<T> actionContinue(ActionHandler handler);

    MenuScreen<T> screen(String title, MenuScreen<T> parent);
    MenuElement<T> createAction(int action, ActionHandler actionHandler);

    MenuElement<T> backAction(Runnable beforeBack);

    MenuElement<T> backAction();

    MenuElement<T> reatart(String title, ActionHandler handler);
    MenuElement<T> menuAction(String title, int action, ActionHandler<MenuElement<T>> handler);

    MenuElement<T> menuAction(String title, ActionHandler<MenuElement<T>> handler);

    MenuElement<T> textHtmlBold(String key, String value);

    MenuElement<T> textHtml(String text);

    MenuElement<T> menuItem(String title, MenuScreen<T> parent, ActionHandler<MenuElement<T>> handler);

    MenuElement<T> badge(int icon, String title);

    MenuElement<T> editText(String title, String value, ActionHandler<MenuElement<T>> handler) ;

    MenuElement<T> highScore(String title, int place, boolean padding);

    MenuElement<T> getItem(String text, boolean padding);
    IOptionsMenuElement<T> selector(String title, int selected, String[] options, boolean toggle, MenuScreen<T> parent, ActionHandler<IOptionsMenuElement<T>> handler) ;
    
}
