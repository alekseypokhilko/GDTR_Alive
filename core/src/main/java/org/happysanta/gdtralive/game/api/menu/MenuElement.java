package org.happysanta.gdtralive.game.api.menu;

/**
 * Author: ch1p
 */
//todo separate interfaces
public interface MenuElement<T> {

    boolean isSelectable();

    T getView();

    void setText(String text);

    default String getText() {
        return null;
    }

    default void performAction(int k) {};

    default void showHelmet() {
    }
}
