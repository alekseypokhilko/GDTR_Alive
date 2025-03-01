package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.text.Html;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.android.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class PlatformMenuElementFactory<T> {
    private AMenu<T> menu;
    private final Application application;

    public PlatformMenuElementFactory(Application application) {
        this.application = application;
    }

    public void setMenu(AMenu<T> menu) {
        this.menu = menu;
    }

    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<T>(beforeAction ? 10 : 20);
    }

    public MenuElement<T> restartAction(String name, ActionHandler handler) {
        return getItdasd(Fmt.colon(s(R.string.restart), name), handler);
    }

//    public MenuElement<T> menuAction(String title, ActionHandler<MenuElement<T>> handler) {
//        return new MenuAction<T>(title, menu, handler);
//    }

    public MenuElement<T> text(String title) {
        return new TextMenuElement(title);
    }

    public MenuElement<T> menuItem(String title, MenuScreen<T> parent) {
        return new MenuItem<>(title, parent, menu);
    }

    public MenuElement<T> actionContinue(ActionHandler handler) {
        return new MenuAction<T>(s(R.string._continue), MenuAction.CONTINUE, menu, handler);
    }

    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new AMenuScreen(title, parent);
    }

    public MenuAction<T> createAction(int action, ActionHandler actionHandler) {
        return new MenuAction<T>(application.getStr().s(MenuUtils.getActionText(action)), action, menu, actionHandler);
    }

    public MenuAction<T> backAction(Runnable beforeBack) {
        return createAction(MenuAction.BACK, item -> {
            beforeBack.run();
            menu.menuBack();
        });
    }

    public MenuAction<T> backAction() {
        return createAction(MenuAction.BACK, item -> menu.menuBack());
    }

    public MenuElement<T> getItdasd(String title, ActionHandler handler) {
        return new MenuAction<T>(title, MenuAction.RESTART, menu, handler);
    }

    public MenuAction<T> menuAction(String title, int action, ActionHandler<MenuElement<T>> handler) {
        return new MenuAction<>(title, action, menu, handler);
    }

    public MenuAction<T> menuAction(String title, ActionHandler<MenuElement<T>> handler) {
        return new MenuAction<>(title, -1, menu, handler);
    }

    public MenuElement<T> textHtmlBold(String key, String value) {
        return new TextMenuElement<>(Html.fromHtml(String.format("<b>%s</b>: %s", key, value == null ? "" : value)));
    }

    public MenuElement<T> textHtml(String text) {
        return new TextMenuElement<>(Html.fromHtml(text));
    }

    public MenuElement<T> menuItem(String title, MenuScreen<T> parent, ActionHandler<MenuElement<T>> handler) {
        return new MenuItem<>(title, parent, menu, handler);
    }

    public MenuElement<T> badge(int icon, String title) {
        return new BadgeWithTextElement<>(icon, title, menu, null);
    }

    public MenuElement<T> editText(String title, String value, ActionHandler<MenuElement<T>> handler) {
        return new InputTextElement<>(title, value, handler);
    }

    public MenuElement<T> highScore(String title, int place, boolean padding) {
        return new HighScoreTextMenuElement<>(title, place, padding);
    }

    public MenuElement<T> getItem(String text, boolean padding) {
        return new HighScoreTextMenuElement<>(Html.fromHtml(text), padding);
    }

}
