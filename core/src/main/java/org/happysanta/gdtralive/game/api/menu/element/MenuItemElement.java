package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.KeyboardHandler;
import org.happysanta.gdtralive.game.api.menu.MenuHandler;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.TouchInterceptor;
import org.happysanta.gdtralive.game.api.menu.view.IMenuHelmetView;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.util.Fmt;

public class MenuItemElement<T> extends ClickableMenuElement<T> implements IMenuItemElement<T> {

    public int x;
    public int y;
    private int value;
    protected MenuScreen<T> screen;
    protected MenuHandler<T> handler;
    protected ActionHandler<IMenuItemElement<T>> action;

    public MenuItemElement(String text, MenuScreen<T> screen, MenuHandler<T> handler, ActionHandler<IMenuItemElement<T>> action,
                           IMenuHelmetView<T> helmetView, IMenuTextView<T> textView, TouchInterceptor<T> touchInterceptor, T layout) {
        super(layout, textView, helmetView, touchInterceptor);
        this.text = Fmt.ra(text);
        this.screen = screen;
        this.handler = handler;
        this.action = action;
        this.textView.setText(getTextForView());
    }

    public MenuItemElement(String text, MenuScreen<T> screen, MenuHandler<T> handler, IMenuHelmetView<T> helmetView, IMenuTextView<T> textView, TouchInterceptor<T> touchInterceptor, T layout) {
        this(text, screen, handler, null, helmetView, textView, touchInterceptor, layout);
    }

    @Override
    public void setText(String s) {
        super.setText(Fmt.ra(s));
    }

    @Override
    public void performAction(int k) {
        switch (k) {
            case KeyboardHandler.KEY_FIRE:
            case KeyboardHandler.KEY_RIGHT:
                if (action != null) {
                    action.handle(this);
                }
                handler.handleAction(this);
                screen.setParent(handler.getCurrentMenu());
                handler.setCurrentMenu(screen);
                break;
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
}
