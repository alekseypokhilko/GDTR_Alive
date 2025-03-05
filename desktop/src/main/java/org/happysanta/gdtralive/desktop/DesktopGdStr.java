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
    }

    public String s(Integer r) {
        return "GDActivity.shared.getString(r)";
    }

    @Override
    public String[] getStringArray(int r) {
        return new String[0];
    }

    @Override
    public String[] getStringArray(S key) {
        return new String[0];
    }

    public String s(S key) {
        switch (key) {
            case CRASHED:
                return "Crashed";
            case FINISHED:
                return "Finished";
            case WHEELIE:
                return "Wheelie";
            case ATTEMPT:
                return "Attempt";
            default:
                return "";
        }
    }
}
