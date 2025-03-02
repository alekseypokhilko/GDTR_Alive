package org.happysanta.gdtralive.game.api.menu.element;

import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuHandler;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;

public interface IOptionsMenuElement<T> extends MenuElement<T>, MenuHandler<T> {
    void setScreen(MenuScreen<T> screen);

    int getUnlockedCount();

    void setUnlockedCount(int k);

    void setOptions(String[] as);

    void setOptions(String[] as, boolean update);

    int getSelectedOption();

    void setSelectedOption(int k);

    boolean _charvZ();

    void update();
}
