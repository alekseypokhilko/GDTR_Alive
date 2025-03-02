package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.text.Html;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.element.AOptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.AToggleMenuElement;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.android.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuActionElement;
import org.happysanta.gdtralive.android.menu.element.AMenuItemElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuFactory;
import org.happysanta.gdtralive.game.api.menu.MenuItemElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.OptionsMenuElement;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.ToggleMenuElement;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class APlatformMenuElementFactory<T> implements PlatformMenuElementFactory<T> {
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };
    private GdMenu<T> menu;
    private final Application application;

    public APlatformMenuElementFactory(Application application) {
        this.application = application;
    }

    public void setMenu(GdMenu<T> menu) {
        this.menu = menu;
    }

    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<>(beforeAction ? 10 : 20);
    }

    public MenuElement<T> restartAction(String name, ActionHandler handler) {
        return reatart(Fmt.colon(s(R.string.restart), name), handler);
    }

    public MenuElement<T> text(String title) {
        return new TextMenuElement<>(title);
    }

    public MenuItemElement<T> menuItem(String title, MenuScreen<T> parent) {
        return new AMenuItemElement<>(title, parent, menu);
    }

    public MenuElement<T> actionContinue(ActionHandler handler) {
        return new MenuActionElement<T>(s(R.string._continue), MenuFactory.CONTINUE, menu, handler);
    }

    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new AMenuScreen<>(title, parent);
    }

    public MenuElement<T> createAction(int action, ActionHandler actionHandler) {
        return new MenuActionElement<T>(application.getStr().s(getActionText(action)), action, menu, actionHandler);
    }

    public MenuElement<T> backAction(Runnable beforeBack) {
        return createAction(MenuFactory.BACK, item -> {
            beforeBack.run();
            menu.menuBack();
        });
    }

    public MenuElement<T> backAction() {
        return createAction(MenuFactory.BACK, item -> menu.menuBack());
    }

    public MenuElement<T> reatart(String title, ActionHandler handler) {
        return new MenuActionElement<T>(title, MenuFactory.RESTART, menu, handler);
    }

    public MenuElement<T> menuAction(String title, int action, ActionHandler<MenuElement<T>> handler) {
        return new MenuActionElement<>(title, action, menu, handler);
    }

    public MenuElement<T> menuAction(String title, ActionHandler<MenuElement<T>> handler) {
        return new MenuActionElement<>(title, -1, menu, handler);
    }

    public MenuElement<T> textHtmlBold(String key, String value) {
        return new TextMenuElement<>(Html.fromHtml(String.format("<b>%s</b>: %s", key, value == null ? "" : value)));
    }

    public MenuElement<T> textHtml(String text) {
        return new TextMenuElement<>(Html.fromHtml(text));
    }

    public MenuItemElement<T> menuItem(String title, MenuScreen<T> parent, ActionHandler<MenuItemElement<T>> handler) {
        return new AMenuItemElement<>(title, parent, menu, handler);
    }

    public MenuElement<T> badge(int icon, String title) {
        return new BadgeWithTextElement<>(ACHIEVEMENT_ICONS[icon], title, menu, null);
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

    public OptionsMenuElement<T> selector(String title, int selected, String[] options, boolean toggle, MenuScreen<T> parent, ActionHandler<OptionsMenuElement<T>> action) {
        return new AOptionsMenuElement<>(title, selected, menu, options, toggle, parent, action);
    }

    public ToggleMenuElement<T> toggle(String title, int selected, String[] options, ActionHandler<ToggleMenuElement<T>> action) {
        return new AToggleMenuElement<>(title, selected, options, action);
    }

    public static int getActionText(int action) {
        int r = 0;
        switch (action) {
            case MenuFactory.BACK:
                r = R.string.back;
                break;

            case MenuFactory.NO:
                r = R.string.no;
                break;

            case MenuFactory.YES:
                r = R.string.yes;
                break;

            case MenuFactory.EXIT:
                r = R.string.exit;
                break;

            case MenuFactory.OK:
                r = R.string.ok;
                break;

            case MenuFactory.PLAY_MENU:
                r = R.string.campaign;
                break;

            case MenuFactory.GO_TO_MAIN:
                r = R.string.go_to_main;
                break;

            case MenuFactory.RESTART:
                r = R.string.restart;
                break;

            case MenuFactory.NEXT:
                r = R.string.next;
                break;

            case MenuFactory.CONTINUE:
                r = R.string._continue;
                break;

            case MenuFactory.LOAD:
                r = R.string.load_this_game;
                break;

            case MenuFactory.INSTALL:
                r = R.string.install_kb;
                break;

            case MenuFactory.DELETE:
                r = R.string.delete;
                break;

            case MenuFactory.RESTART_WITH_NEW_LEVEL:
                r = R.string.restart_with_new_level;
                break;

            case MenuFactory.SEND_LOGS:
                r = R.string.send_logs;
                break;

            case MenuFactory.LIKE:
                r = R.string.like;
                break;

        }
        return r;
    }
}
