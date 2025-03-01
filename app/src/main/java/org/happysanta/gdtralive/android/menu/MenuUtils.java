package org.happysanta.gdtralive.android.menu;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.element.MenuAction;

public class MenuUtils {

    public static int getActionText(int action) {
        int r = 0;
        switch (action) {
            case MenuAction.BACK:
                r = R.string.back;
                break;

            case MenuAction.NO:
                r = R.string.no;
                break;

            case MenuAction.YES:
                r = R.string.yes;
                break;

            case MenuAction.EXIT:
                r = R.string.exit;
                break;

            case MenuAction.OK:
                r = R.string.ok;
                break;

            case MenuAction.PLAY_MENU:
                r = R.string.campaign;
                break;

            case MenuAction.GO_TO_MAIN:
                r = R.string.go_to_main;
                break;

            case MenuAction.RESTART:
                r = R.string.restart;
                break;

            case MenuAction.NEXT:
                r = R.string.next;
                break;

            case MenuAction.CONTINUE:
                r = R.string._continue;
                break;

            case MenuAction.LOAD:
                r = R.string.load_this_game;
                break;

            case MenuAction.INSTALL:
                r = R.string.install_kb;
                break;

            case MenuAction.DELETE:
                r = R.string.delete;
                break;

            case MenuAction.RESTART_WITH_NEW_LEVEL:
                r = R.string.restart_with_new_level;
                break;

            case MenuAction.SEND_LOGS:
                r = R.string.send_logs;
                break;

            case MenuAction.LIKE:
                r = R.string.like;
                break;

        }
        return r;
    }
}
