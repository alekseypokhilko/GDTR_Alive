package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.util.BiFunction;

public interface MenuScreen<T> {
    void setTitle(String s);

    void addItem(MenuElement<T> item);

    MenuScreen<T> getParent();
    void setParent(MenuScreen<T> target);

    MenuElement<T> getActions(int action);
    void performAction(int k);

    MenuScreen<T> setBeforeShowAction(Runnable beforeShowAction);

    void performBeforeShowAction();

    void onShow();

    void clear();

    MenuScreen<T> builder(BiFunction<MenuScreen<T>, MenuData, MenuScreen<T>> builder);

    default MenuScreen<T> build() {
        return build(null);
    }

    MenuScreen<T> build(MenuData data);

    void setIsTextScreen(boolean isTextScreen);

    T getLayout();
    void setSelected(int index);
    void onScroll(double percent);
    void resetHighlighted();
    void highlightElement();
}
