package org.happysanta.gdtralive.game.external;

import org.happysanta.gdtralive.game.visual.Strings;

public interface GdUtils {
    int[][] copyArray(int[][] original);
    int getRandom(int origin, int bound);
    String s(Integer r);
    String s(Strings key);
}
