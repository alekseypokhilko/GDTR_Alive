package org.happysanta.gdtralive.game.api.menu;

public interface ViewUtils<T> {
    boolean inViewBounds(T view, int x, int y);

    void setSelected(T view, boolean selected);
}
