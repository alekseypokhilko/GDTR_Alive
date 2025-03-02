package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;

public interface IMenuActionElement<T> extends MenuElement<T> {
    void setLock(boolean isLocked, boolean isBlackLock);
}
