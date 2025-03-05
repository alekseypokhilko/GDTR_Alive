package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.util.BiFunction;

public class FakeMenuScreen<T> implements MenuScreen<T>{
    GdMenu<T> menu;

    public FakeMenuScreen(GdMenu<T> menu) {
        this.menu = menu;
    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public void add(MenuElement<T> item) {

    }

    @Override
    public MenuScreen<T> getParent() {
        menu.menuToGame();
        return this;
    }

    @Override
    public void setParent(MenuScreen<T> target) {

    }

    @Override
    public MenuElement<T> getActions(int action) {
        return null;
    }

    @Override
    public void performAction(int k) {

    }

    @Override
    public MenuScreen<T> setBeforeShowAction(Runnable beforeShowAction) {
        return this;
    }

    @Override
    public void performBeforeShowAction() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void clear() {

    }

    @Override
    public MenuScreen<T> builder(BiFunction<MenuScreen<T>, MenuData, MenuScreen<T>> builder) {
        return this;
    }

    @Override
    public MenuScreen<T> build(MenuData data) {
        return this;
    }

    @Override
    public void setIsTextScreen(boolean isTextScreen) {

    }

    @Override
    public T getLayout() {
        return (T) this;
    }

    @Override
    public void setSelected(int index) {

    }

    @Override
    public void onScroll(double percent) {

    }

    @Override
    public void resetHighlighted() {

    }

    @Override
    public void highlightElement() {

    }
}
