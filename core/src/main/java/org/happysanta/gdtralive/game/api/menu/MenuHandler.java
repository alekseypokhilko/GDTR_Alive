package org.happysanta.gdtralive.game.api.menu;

public interface MenuHandler<T> {

    default MenuScreen<T> getCurrentMenu() {
        return null;
    }

    default void setCurrentMenu(MenuScreen<T> e) {

    }

    default void handleAction(MenuElement<T> item) {

    }

    default void menuBack() {}
}
