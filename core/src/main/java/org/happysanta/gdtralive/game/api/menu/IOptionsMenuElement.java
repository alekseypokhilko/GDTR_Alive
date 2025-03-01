package org.happysanta.gdtralive.game.api.menu;

public interface IOptionsMenuElement<T> extends MenuElement<T>, MenuHandler<T> {
    void setScreen(MenuScreen<T> screen);
}
