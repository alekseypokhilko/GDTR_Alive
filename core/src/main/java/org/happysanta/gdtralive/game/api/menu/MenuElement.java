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

    void performAction(int k);

    default void showHelmet() {
    }

    default int getUnlockedCount() {
        return 0;
    }

    default void setUnlockedCount(int k) {
    }

    default String[] getOptions() {
        return new String[]{""};
    }

    default void setOptions(String[] as) {
        setOptions(as, true);
    }

    default void setOptions(String[] as, boolean update) {
    }

    default int getSelectedOption() {
        return 0;
    }

    default void setSelectedOption(int k) {
    }

    default boolean _charvZ() {
        return false;
    }

    default void update(){}
    default int getValue() {
        return 0;
    }

    default void setValue(int value) {
    }
}
