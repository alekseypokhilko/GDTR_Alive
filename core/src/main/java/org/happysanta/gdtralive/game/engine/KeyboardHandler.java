package org.happysanta.gdtralive.game.engine;

import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdMenu;

public class KeyboardHandler {
    public static final int KEY_FIRE = 5;
    public static final int KEY_UP = 2;
    public static final int KEY_DOWN = 8;
    public static final int KEY_LEFT = 4;
    public static final int KEY_RIGHT = 6;
    private static final byte[][] m_DaaB = {{0, 0}, {1, 0}, {0, -1}, {0, 0}, {0, 0}, {0, 1}, {-1, 0}};
    private static final byte[][][] m_maaaB = {
            {{0, 0}, {1, -1}, {1, 0}, {1, 1}, {0, -1}, {-1, 0}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}},
            {{0, 0}, {1, 0}, {0, 0}, {0, 0}, {-1, 0}, {0, -1}, {0, 1}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 0}, {0, 0}, {0, 0}, {0, 0}}
    };

    private final Engine engine;
    private GdMenu menu;
    private final GdApplication application;

    private int inputOption;
    private final boolean[] buttons_m_aeaZ;
    private final boolean[] buttons_m_LaZ;

    public KeyboardHandler(GdApplication application, Engine engine, int selectedInputOption) {
        this.engine = engine;
        this.application = application;

        inputOption = 2;
        buttons_m_aeaZ = new boolean[7];
        buttons_m_LaZ = new boolean[10];
        setInputOption(selectedInputOption);
    }

    public void setInputOption(int option) {
        inputOption = option;
    }

    public void resetButtonsTouch() {
        for (int j = 0; j < 10; j++)
            buttons_m_LaZ[j] = false;

        for (int k = 0; k < 7; k++)
            buttons_m_aeaZ[k] = false;

    }

    public synchronized void keyPressed(int j) {
        if (application.isMenuShown() && menu != null)
            menu.keyPressed(j);
        processKeyPressed(j);
    }

    public synchronized void keyReleased(int j) {
        processKeyReleased(j);
    }

    private void processKeyPressed(int j) {
        int k = getGameAction(j);
        int l;
        if ((l = j - 48) >= 0 && l < 10)
            buttons_m_LaZ[l] = true;
        else if (k >= 0 && k < 7)
            buttons_m_aeaZ[k] = true;
        processKeyPressed();
    }

    private void processKeyPressed() {
        int j = 0;
        int k = 0;
        int l = inputOption;
        for (int i1 = 0; i1 < 10; i1++)
            if (buttons_m_LaZ[i1]) {
                j += m_maaaB[l][i1][0];
                k += m_maaaB[l][i1][1];
            }

        for (int j1 = 0; j1 < 7; j1++)
            if (buttons_m_aeaZ[j1]) {
                j += m_DaaB[j1][0];
                k += m_DaaB[j1][1];
            }

        engine.processKeyPressed(j, k);
    }

    private void processKeyReleased(int j) {
        int k = getGameAction(j);
        int l;
        if ((l = j - 48) >= 0 && l < 10)
            buttons_m_LaZ[l] = false;
        else if (k >= 0 && k < 7)
            buttons_m_aeaZ[k] = false;
        processKeyPressed();
    }

    public static int getGameAction(int key) {
        switch (key) {
            case 50: // 2
                return KEY_UP; // up
            case 56: // 8
                return KEY_DOWN; // down
            case 52: // 4
                return KEY_LEFT; // left
            case 54: // 6
                return KEY_RIGHT; // right
            case 53: // 5
                return KEY_FIRE; // fire
        }
        return 0;
    }

    public void setMenu(GdMenu menu) {
        this.menu = menu;
    }
}
