package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;

public interface MenuItemElement<T> extends MenuElement<T> {
    int getValue();

    void setValue(int value);
}
