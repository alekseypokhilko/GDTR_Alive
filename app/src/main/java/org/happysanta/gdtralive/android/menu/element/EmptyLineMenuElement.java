package org.happysanta.gdtralive.android.menu.element;

import static org.happysanta.gdtralive.android.Helpers.getDp;
import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.view.ViewGroup;

import org.happysanta.gdtralive.android.menu.views.MenuTextView;
import org.happysanta.gdtralive.game.api.menu.MenuElement;

public class EmptyLineMenuElement<T> implements MenuElement<T> {

    protected String text;
    protected int offset;
    protected MenuTextView view;

    public EmptyLineMenuElement(int offset) {
        this.offset = offset;

        view = new MenuTextView(getGDActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDp(offset)));
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
