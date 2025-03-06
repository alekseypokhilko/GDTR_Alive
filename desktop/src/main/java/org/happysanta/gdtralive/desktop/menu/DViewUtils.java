package org.happysanta.gdtralive.desktop.menu;

import org.happysanta.gdtralive.game.api.menu.ViewUtils;

public class DViewUtils<T> implements ViewUtils<T> {

    public boolean inViewBounds(T view, int x, int y) {
        return true;
    }

    public void setSelected(T view, boolean selected) {

    }
}
