package org.happysanta.gdtralive.android.menu;

import android.graphics.Rect;
import android.view.View;

import org.happysanta.gdtralive.game.api.menu.ViewUtils;

public class AViewUtils<T> implements ViewUtils<T> {

    public boolean inViewBounds(T view, int x, int y) {
        Rect rect = new Rect();
        int[] location = new int[2];

        ((View) view).getDrawingRect(rect);
        ((View) view).getLocationOnScreen(location);
        rect.offset(location[0], location[1]);

        return rect.contains(x, y);
    }

    public void setSelected(T view, boolean selected) {
        ((View) view).setSelected(selected);
    }
}
