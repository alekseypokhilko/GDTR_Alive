package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.engine.Engine;

import java.util.HashMap;
import java.util.Map;

public class KeyboardHandler {
    public static final int KEY_FIRE = 5;
    public static final int KEY_UP = 2;
    public static final int KEY_DOWN = 8;
    public static final int KEY_LEFT = 4;
    public static final int KEY_RIGHT = 6;
    private static final byte[][] m_DaaB = {{0, 0}, {1, 0}, {0, -1}, {0, 0}, {0, 0}, {0, 1}, {-1, 0}};
    /**
     * [buttons 0 - 9][keySet][{gaz, clockwiseRotation}]
     *  1 = applied force (forward)
     *  0 = no force
     * -1 = negative force (backward)
     */
    private static final byte[][][] keySetMapping = {
            {{0, 0}, {1, -1}, {1, 0}, {1, 1}, {0, -1}, {-1, 0}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}},
            {{0, 0}, {1, 0}, {0, 0}, {0, 0}, {-1, 0}, {0, -1}, {0, 1}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 0}, {0, 0}, {0, 0}, {0, 0}}
    };

    private static final Map<Integer, Integer> keyMapping = new HashMap<>();

    private final Engine engine;
    private GdMenu menu;
    private final GdApplication application;

    private int inputOption;
    private final boolean[] buttons_m_aeaZ;
    private final boolean[] pressedButtons;

    public KeyboardHandler(GdApplication application, Engine engine, int selectedInputOption) {
        this.engine = engine;
        this.application = application;

        inputOption = 0;
        buttons_m_aeaZ = new boolean[7];
        pressedButtons = new boolean[10];
        setInputOption(selectedInputOption);

        keyMapping.put(100, 50); //gamepad Y
        keyMapping.put(99, 56); //gamepad X
        keyMapping.put(102, 52); //gamepad LB
        keyMapping.put(103, 54); //gamepad RB
//        keyMapping.put(97, 4); //gamepad B
        keyMapping.put(96, 53); //gamepad A

        keyMapping.put(19, 50); //w
        keyMapping.put(20, 56); //s
        keyMapping.put(21, 52); //a
        keyMapping.put(22, 54); //d
        keyMapping.put(66, 53); //enter
    }

    public void setInputOption(int option) {
        inputOption = option;
    }

    public void resetButtonsTouch() {
        for (int j = 0; j < 10; j++) {
            pressedButtons[j] = false;
        }

        for (int k = 0; k < 7; k++)
            buttons_m_aeaZ[k] = false;

    }
    public void mappedKeyPressed(int j) {
        if (104 == j || 105 == j || 109 == j || 108 == j || 107 == j) {
            if (j == 109) { //start
                if (application.isMenuShown()) {
                    application.menuToGame();
                } else {
                    application.gameToMenu();
                }
            }
            if (j == 108) {
                application.getGame().restart(true);
            }
            if (j == 107) {
                application.getGame().handleSetSavepointAction();
            }
            if (j == 104) {
                pressedButtons[1] = true;
            }
            if (j == 105) {
                pressedButtons[3] = true;
            }
            processKeyPressed();
        } else {
            Integer code = keyMapping.get(j);
            keyPressed(code == null ? 0 : code);
        }
    }

    public  void mappedKeyReleased(int j) {
        if (104 == j || 105 == j) {
            if (j == 104) {
                pressedButtons[1] = false;
            }
            if (j == 105) {
                pressedButtons[3] = false;
            }
            processKeyPressed();
        } else {
            Integer code = keyMapping.get(j);
            keyReleased(code == null ? 0 : code);
        }
    }

    public  void keyPressed(int j) {
        if (application.isMenuShown() && menu != null)
            menu.keyPressed(j);

        int k = getGameAction(j);
        int buttonNumber;
        if ((buttonNumber = j - 48) >= 0 && buttonNumber < 10) {
            pressedButtons[buttonNumber] = true;
        } else if (k >= 0 && k < 7) {
            buttons_m_aeaZ[k] = true;
        }
        processKeyPressed();
    }

    public  void keyReleased(int j) {
        int k = getGameAction(j);
        int buttonNumber;
        if ((buttonNumber = j - 48) >= 0 && buttonNumber < 10) {
            pressedButtons[buttonNumber] = false;
        } else if (k >= 0 && k < 7) {
            buttons_m_aeaZ[k] = false;
        }
        processKeyPressed();
    }

    private void processKeyPressed() {
        int gaz = 0;
        int tilt = 0;
        int l = inputOption;
        for (int buttonNumber = 0; buttonNumber < 10; buttonNumber++) {
            if (pressedButtons[buttonNumber]) {
                gaz += keySetMapping[l][buttonNumber][0];
                tilt += keySetMapping[l][buttonNumber][1];
            }
        }
        for (int j1 = 0; j1 < 7; j1++) {
            if (buttons_m_aeaZ[j1]) {
                gaz += m_DaaB[j1][0];
                tilt += m_DaaB[j1][1];
            }
        }
        engine.processKeyPressed(gaz, tilt);
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
