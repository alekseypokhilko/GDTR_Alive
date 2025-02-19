package org.happysanta.gdtralive.game.external;

import org.happysanta.gdtralive.game.visual.Strings;

public interface GdUtils {
    int[][] copyArray(int[][] original);
    int getRandom(int origin, int bound);
    String s(Integer r);
    String s(Strings key);

    static String getDurationString(long millis) {
        return String.format("%d:%02d.%03d", millis / 60000, (millis / 1000) % 60, millis % 1000);
    }

    static int unpackInt(int i) {
        return (i << 16) >> 3;
    }

    static int packInt(int i) {
        return (i << 3) >> 16;
    }
}
