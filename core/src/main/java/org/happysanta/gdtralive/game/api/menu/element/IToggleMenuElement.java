package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;

public interface IToggleMenuElement<T> extends MenuElement<T> {
    int getSelectedOption();
}
