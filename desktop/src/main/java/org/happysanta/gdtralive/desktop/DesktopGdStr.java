package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.Strings;

import java.util.HashMap;
import java.util.Map;

public class DesktopGdStr implements GdStr {

    private final Map<Strings, Integer> strings = new HashMap<>();

    public DesktopGdStr() {
        strings.put(Strings.CRASHED, 1);
        strings.put(Strings.FINISHED, 1);
        strings.put(Strings.WHEELIE, 1);
    }

    public String s(Integer r) {
        return "GDActivity.shared.getString(r)";
    }

    public String s(Strings key) {
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
