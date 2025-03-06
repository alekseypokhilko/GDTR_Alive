package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.S;

import java.util.HashMap;
import java.util.Map;

public class DesktopGdStr implements GdStr {

    private final Map<S, Integer> strings = new HashMap<>();

    public DesktopGdStr() {
        strings.put(S.CRASHED, 1);
        strings.put(S.FINISHED, 1);
        strings.put(S.WHEELIE, 1);
        strings.put(S.play, 1);
        strings.put(S.competition, 1);
        strings.put(S.workshop, 1);
        strings.put(S.profile, 1);
        strings.put(S.options, 1);
    }

    public String s(Integer r) {
        if (0 == r) return "OK";
        if (1 == r) return "BACK";
        if (2 == r) return "EXIT";
        if (3 == r) return "YES";
        if (4 == r) return "NO";
        if (5 == r) return "PLAY_MENU";
        if (6 == r) return "GO_TO_MAIN";
        if (7 == r) return "RESTART";
        if (8 == r) return "NEXT";
        if (9 == r) return "CONTINUE";
        if (10 == r) return "INSTALL";
        if (11 == r) return "LOAD";
        if (12 == r) return "SELECT_FILE";
        if (13 == r) return "DELETE";
        if (14 == r) return "RESTART_WITH_NEW_LEVEL";
        if (15 == r) return "SEND_LOGS";
        if (16 == r) return "LIKE";
        return "FIX ME!";
    }

    @Override
    public String[] getStringArray(int r) {
        return new String[0];
    }

    @Override
    public String[] getStringArray(S key) {
        if (key == S.keyset) {
            return new String[] {"1", "2", "3"};
        }
        return new String[0];
    }

    public String s(S key) {
        switch (key) {
            case competition:
                return "Play";
            case profile:
                return "Profile";
            case play:
                return "Play";
            case options:
                return "Options";
            case workshop:
                return "Workshop";
            case CRASHED:
                return "Crashed";
            case FINISHED:
                return "Finished";
            case WHEELIE:
                return "Wheelie";
            case ATTEMPT:
                return "Attempt";
            default:
                return key.toString();
        }
    }
}
