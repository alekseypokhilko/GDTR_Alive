package org.happysanta.gdtralive.android.menu;

import org.happysanta.gdtralive.android.menu.api.MenuElement;

public interface MenuHandler {

    default AMenuScreen getCurrentMenu() {
        return null;
    }

    default void setCurrentMenu(AMenuScreen e) {

    }

    default void handleAction(MenuElement item) {

    }
}
