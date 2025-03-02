package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.view.IMenuTextView;

public class EmptyLineMenuElement<T> implements MenuElement<T> {

    protected String text;
    protected IMenuTextView<T> view;

    public EmptyLineMenuElement(IMenuTextView<T> emptyLineElement) {
        this.view = emptyLineElement;
    }

    @Override
    public T getView() {
        return (T) view;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void setText(String s) {
        text = s;
    }

}
