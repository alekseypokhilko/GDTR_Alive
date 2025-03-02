package org.happysanta.gdtralive.game.api.menu;

import org.happysanta.gdtralive.game.api.menu.element.ClickableMenuElement;

public class TouchInterceptor<T> {
    private ClickableMenuElement<T> target;

    public void setTarget(ClickableMenuElement<T> target) {
        this.target = target;
    }

    public boolean onTouch(T view, int action, int rawX, int rawY, ViewUtils<T> viewUtils) {
        if (target == null) {
            return false;
        }
        return target.onTouch(view, action, rawX, rawY, viewUtils);
    }
}
