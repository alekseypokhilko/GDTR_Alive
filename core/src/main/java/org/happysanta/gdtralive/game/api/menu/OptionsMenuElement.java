package org.happysanta.gdtralive.game.api.menu;

public interface OptionsMenuElement<T> extends MenuElement<T>, MenuHandler<T> {
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
