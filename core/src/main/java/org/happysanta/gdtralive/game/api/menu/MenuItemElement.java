package org.happysanta.gdtralive.game.api.menu;

public interface MenuItemElement<T> extends MenuElement<T> {
    int getValue();

    void setValue(int value);
}
