package org.happysanta.gdtralive.game.visual;

import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.mod.PackLevel;

public class Fmt {
    public static String colon(String label, String name) {
        return String.format("%s: %s", label, name);
    }

    public static String colon(String label) {
        return String.format("%s: ", label);
    }

    public static String ra(String label) {
        return String.format("%s>", label);
    }

    public static String sp(String left, String right) {
        return String.format("%s %s", left, right);
    }

    public static String us(String left, String right) {
        return String.format("%s_%s", left, right);
    }

    public static String formatLevelsCount(Mod mod) {
        StringBuilder s = new StringBuilder();
        for (PackLevel level : mod.getLevels()) {
            s.append(level.getTracks().size()).append(" ");
        }
        return s.toString().trim();
    }
}
