package org.happysanta.gdtralive.android.menu;

import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.modes.MenuData;

public interface InGameScreenProvider {
    MenuScreen getInGameScreen(MenuData data);
    GameMode getInGameScreenMode();
}
