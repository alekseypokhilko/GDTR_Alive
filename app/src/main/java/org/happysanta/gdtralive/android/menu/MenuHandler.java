package org.happysanta.gdtralive.android.menu;

import org.happysanta.gdtralive.android.menu.element.MenuElement;

public interface MenuHandler {

    default MenuScreen getCurrentMenu() {
        return null;
    }

    default void setCurrentMenu(MenuScreen e) {

    }

    default void handleAction(MenuElement item) {

    }
}
