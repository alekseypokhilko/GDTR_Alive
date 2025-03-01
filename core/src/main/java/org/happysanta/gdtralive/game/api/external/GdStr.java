package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.S;

public interface GdStr {
    String s(Integer r);
    String[] getStringArray(int r);
    String[] getStringArray(S key);
    String s(S key);
}
