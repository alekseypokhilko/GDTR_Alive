package org.happysanta.gdtralive.android;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.visual.Strings;

import java.util.HashMap;
import java.util.Map;

public class AndroidGdUtils implements GdUtils {

    private final Map<Strings, Integer> strings = new HashMap<>();

    public AndroidGdUtils() {
        strings.put(Strings.CRASHED, R.string.crashed);
        strings.put(Strings.FINISHED, R.string.finished1);
        strings.put(Strings.WHEELIE, R.string.wheelie);
        strings.put(Strings.ATTEMPT, R.string.attempt);
        strings.put(Strings.TRIAL_MASTER, R.string.trial_master);
        strings.put(Strings.TRIAL_MASTER_DESC, R.string.trial_master_description);
        strings.put(Strings.GAMBLER, R.string.gambler);
        strings.put(Strings.GAMBLER_DESC, R.string.gambler_description);
        strings.put(Strings.ESTHETE, R.string.esthete);
        strings.put(Strings.ESTHETE_DESC, R.string.esthete_description);
        strings.put(Strings.SERIES_LOVER, R.string.developer);
        strings.put(Strings.SERIES_LOVER_DESC, R.string.developer);
        strings.put(Strings.DEVELOPER, R.string.developer);
        strings.put(Strings.DEVELOPER_DESC, R.string.developer_description);
        strings.put(Strings.BACK_TO_SCHOOL, R.string.back_to_school);
        strings.put(Strings.BACK_TO_SCHOOL_DESC, R.string.back_to_school_description);
    }

    public String s(Integer r) {
        return GDActivity.shared.getString(r);
    }

    public String s(Strings key) {
        return s(strings.get(key));
    }
}
